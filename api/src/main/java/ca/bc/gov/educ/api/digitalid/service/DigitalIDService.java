package ca.bc.gov.educ.api.digitalid.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
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

  private final static DigitalIDMapper mapper = DigitalIDMapper.mapper;
  
  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDRepository digitalIDRepository;
  
  @Getter(AccessLevel.PRIVATE)
  private final AccessChannelCodeTableRepository accessChannelCodeTableRepo;
  
  @Getter(AccessLevel.PRIVATE)
  private final IdentityTypeCodeTableRepository identityTypeCodeTableRepo;
  
  public static final String PARAMETERS = "parameters";
  private static Map<String, AccessChannelCode> accessChannelCodeMap = new ConcurrentHashMap<>();
  private static Map<String, IdentityTypeCode> identityTypeCodeMap = new ConcurrentHashMap<>();
  
  @Autowired
  DigitalIDService(final DigitalIDRepository digitalIDRepository, final AccessChannelCodeTableRepository accessChannelCodeTableRepo, final IdentityTypeCodeTableRepository identityTypeCodeTableRepo, ApplicationProperties props) {
    this.digitalIDRepository = digitalIDRepository;
    this.accessChannelCodeTableRepo = accessChannelCodeTableRepo;
    this.identityTypeCodeTableRepo = identityTypeCodeTableRepo;
  }

  @PreDestroy
  public void close() {
    accessChannelCodeMap.clear();
    identityTypeCodeMap.clear();
  }

  @PostConstruct
  public void loadCodeTableDataToMemory() {
    log.info("Loading digital ID code table data to memory.");
    getAccessChannelCodesList();
    getIdentityTypeCodesList();
    log.info("Loading digital ID code table data to memory completed.");
  }
  
  /**
   * Returns the full list of access channel codes
   * 
   * @return
   */
  public Map<String, AccessChannelCode> getAccessChannelCodesList() {
	  if(accessChannelCodeMap == null) {
		  accessChannelCodeMap = accessChannelCodeTableRepo.findAll().stream().map(mapper::toStructure).collect(Collectors.toList()).stream().collect(Collectors.toMap(AccessChannelCode::getAccessChannelCode, Function.identity()));
	  }
	  return accessChannelCodeMap;
  }
  
  /**
   * Returns the full list of access channel codes
   * 
   * @return
   */
  public Map<String, IdentityTypeCode> getIdentityTypeCodesList() {
	  if(identityTypeCodeMap == null) {
		  identityTypeCodeMap = identityTypeCodeTableRepo.findAll().stream().map(mapper::toStructure).collect(Collectors.toList()).stream().collect(Collectors.toMap(IdentityTypeCode::getIdentityTypeCode, Function.identity()));
	  }
	  return identityTypeCodeMap;
  }

  public AccessChannelCode findAccessChannelCode(String accessChannelCode) {
    if (accessChannelCodeMap.containsKey(accessChannelCode)) {
      return accessChannelCodeMap.get(accessChannelCode);
    }
    getAccessChannelCodesList();
    return accessChannelCodeMap.get(accessChannelCode);
  }
  
  public IdentityTypeCode findIdentityTypeCode(String identityTypeCode) {
    if (identityTypeCodeMap.containsKey(identityTypeCode)) {
      return identityTypeCodeMap.get(identityTypeCode);
    }
    getIdentityTypeCodesList();
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
