package ca.bc.gov.educ.api.digitalid.service;

import static ca.bc.gov.educ.api.digitalid.constant.CodeTableConstants.ACCESS_CHANNEL_CODE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.rest.RestUtils;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.IdentityTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * DigitalIDService
 *
 * @author John Cox
 */

@Service
@Slf4j
public class DigitalIDService {
  private static final String DIGITAL_ID_ATTRIBUTE = "digitalID";

  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDRepository digitalIDRepository;
  
  @Getter(AccessLevel.PRIVATE)
  private final AccessChannelCodeTableRepository accessChannelCodeTableRepo;
  
  @Getter(AccessLevel.PRIVATE)
  private final IdentityTypeCodeTableRepository identityTypeCodeTableRepo;
  
  public static final String PARAMETERS = "parameters";
  @Getter(AccessLevel.PRIVATE)
  private final RestUtils restUtils;
  private static Map<String, AccessChannelCode> lastAccessChannelCodeMap = new ConcurrentHashMap<>();
  private static Map<String, IdentityTypeCode> identityTypeCodeMap = new ConcurrentHashMap<>();
  private final ApplicationProperties props;
  
  @Autowired
  DigitalIDService(final DigitalIDRepository digitalIDRepository, final AccessChannelCodeTableRepository accessChannelCodeTableRepo, final IdentityTypeCodeTableRepository identityTypeCodeTableRepo, final RestUtils restUtils, ApplicationProperties props) {
    this.digitalIDRepository = digitalIDRepository;
    this.accessChannelCodeTableRepo = accessChannelCodeTableRepo;
    this.identityTypeCodeTableRepo = identityTypeCodeTableRepo;
    this.restUtils = restUtils;
    this.props = props;
  }

  @PreDestroy
  public void close() {
    lastAccessChannelCodeMap.clear();
    identityTypeCodeMap.clear();
  }

  @PostConstruct
  public void loadCodeTableDataToMemory() {
    log.info("Loading digital ID code table data to memory.");
    loadAccessChannelCodes();
    loadIdentityTypeCodes();
    log.info("Loading digital ID code table data to memory completed.");
  }
  
  public void loadIdentityTypeCodes() {
    RestTemplate restTemplate = restUtils.getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    ResponseEntity<IdentityTypeCode[]> identityTypeCodeResponse;
    identityTypeCodeResponse = restTemplate.exchange(props.getCodeTableApiURL() + ACCESS_CHANNEL_CODE.getValue(), HttpMethod.GET, new HttpEntity<>(PARAMETERS, headers), IdentityTypeCode[].class);
    if (identityTypeCodeResponse.getBody() != null) {
    	identityTypeCodeMap.putAll(Arrays.stream(identityTypeCodeResponse.getBody()).collect(Collectors.toMap(IdentityTypeCode::getIdentityTypeCode, dataSource -> dataSource)));
    }
  }

  public void loadAccessChannelCodes() {
    RestTemplate restTemplate = restUtils.getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    ResponseEntity<AccessChannelCode[]> accessChannelCodeResponse;
    accessChannelCodeResponse = restTemplate.exchange(props.getCodeTableApiURL() + ACCESS_CHANNEL_CODE.getValue(), HttpMethod.GET, new HttpEntity<>(PARAMETERS, headers), AccessChannelCode[].class);
    if (accessChannelCodeResponse.getBody() != null) {
      lastAccessChannelCodeMap.putAll(Arrays.stream(accessChannelCodeResponse.getBody()).collect(Collectors.toMap(AccessChannelCode::getAccessChannelCode, dataSource -> dataSource)));
    }
  }

  @Cacheable("accessChannelCodeCache")
  public AccessChannelCode findAccessChannelCode(String accessChannelCode) {
    if (lastAccessChannelCodeMap.containsKey(accessChannelCode)) {
      return lastAccessChannelCodeMap.get(accessChannelCode);
    }
    loadAccessChannelCodes();
    return lastAccessChannelCodeMap.get(accessChannelCode);
  }
  
  @Cacheable("identityTypeCodeCache")
  public IdentityTypeCode findIdentityTypeCode(String identityTypeCode) {
    if (identityTypeCodeMap.containsKey(identityTypeCode)) {
      return identityTypeCodeMap.get(identityTypeCode);
    }
    loadIdentityTypeCodes();
    return identityTypeCodeMap.get(identityTypeCode);
  }
  
  /**
   * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
   *
   * @param typeValue
   * @param typeCode
   * @return
   * @throws EntityNotFoundException
   */
  public DigitalIDEntity searchDigitalId(String typeCode, String typeValue) {

    Optional<DigitalIDEntity> result = getDigitalIDRepository().findByIdentityTypeCodeAndIdentityValue(typeCode.toUpperCase(), typeValue.toUpperCase());

    if (result.isPresent()) {
      return result.get();
    } else {
      throw new EntityNotFoundException(DigitalIDEntity.class, "identityTypeCode", typeCode, "identityTypeValue", typeValue);
    }
  }

  /**
   * Search for DigitalIDEntity by digital id
   *
   * @param id
   * @return
   * @throws EntityNotFoundException
   */
  public DigitalIDEntity retrieveDigitalID(UUID id) {
    Optional<DigitalIDEntity> result = getDigitalIDRepository().findById(id);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new EntityNotFoundException(DigitalIDEntity.class, DIGITAL_ID_ATTRIBUTE, id.toString());
    }
  }

  /**
   * Creates a DigitalIDEntity
   *
   * @param digitalID
   * @return
   * @throws EntityNotFoundException
   * @throws InvalidParameterException
   */
  public DigitalIDEntity createDigitalID(DigitalIDEntity digitalID) {

    validateCreateParameters(digitalID);

    if (digitalID.getDigitalID() != null) {
      throw new InvalidParameterException(DIGITAL_ID_ATTRIBUTE);
    }
    digitalID.setUpdateDate(new Date());
    digitalID.setCreateDate(new Date());

    return digitalIDRepository.save(digitalID);
  }
  
  /**
   * Returns the full list of access channel codes
   * 
   * @return
   */
  public List<AccessChannelCodeEntity> getAccessChannelCodesList() {
    return accessChannelCodeTableRepo.findAll();
  }
  
  /**
   * Returns the full list of access channel codes
   * 
   * @return
   */
  public List<IdentityTypeCodeEntity> getIdentityTypeCodesList() {
    return identityTypeCodeTableRepo.findAll();
  }

  /**
   * Updates a DigitalIDEntity
   *
   * @param digitalID
   * @return
   * @throws Exception
   */
  public DigitalIDEntity updateDigitalID(DigitalIDEntity digitalID) {

    validateCreateParameters(digitalID);

    Optional<DigitalIDEntity> curDigitalID = digitalIDRepository.findById(digitalID.getDigitalID());

    if (curDigitalID.isPresent()) {
      DigitalIDEntity newDigitalID = curDigitalID.get();
      newDigitalID.setStudentID(digitalID.getStudentID());
      newDigitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode());
      newDigitalID.setIdentityValue(digitalID.getIdentityValue());
      newDigitalID.setLastAccessDate(digitalID.getLastAccessDate());
      newDigitalID.setLastAccessChannelCode(digitalID.getLastAccessChannelCode());
      newDigitalID.setUpdateDate(new Date());
      newDigitalID = digitalIDRepository.save(newDigitalID);

      return newDigitalID;
    } else {
      throw new EntityNotFoundException(DigitalIDEntity.class, DIGITAL_ID_ATTRIBUTE, digitalID.getDigitalID().toString());
    }
  }

  private void validateCreateParameters(DigitalIDEntity digitalIDEntity) {
    if (digitalIDEntity.getCreateDate() != null)
      throw new InvalidParameterException("createDate");
    if (digitalIDEntity.getUpdateDate() != null)
      throw new InvalidParameterException("updateDate");
  }

}
