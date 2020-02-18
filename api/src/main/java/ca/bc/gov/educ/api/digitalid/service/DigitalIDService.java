package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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


  @Autowired
  DigitalIDService(final DigitalIDRepository digitalIDRepository, final AccessChannelCodeTableRepository accessChannelCodeTableRepo, final IdentityTypeCodeTableRepository identityTypeCodeTableRepo) {
    this.digitalIDRepository = digitalIDRepository;
    this.accessChannelCodeTableRepo = accessChannelCodeTableRepo;
    this.identityTypeCodeTableRepo = identityTypeCodeTableRepo;
  }


  /**
   * Returns the full list of access channel codes
   *
   * @return {@link List<AccessChannelCodeEntity>}
   */
  @Cacheable("accessChannelCodes")
  public List<AccessChannelCodeEntity> getAccessChannelCodesList() {
    return accessChannelCodeTableRepo.findAll();
  }

  /**
   * Returns the full list of access channel codes
   *
   * @return {@link List<IdentityTypeCodeEntity>}
   */
  @Cacheable("identityTypeCodes")
  public List<IdentityTypeCodeEntity> getIdentityTypeCodesList() {
    return identityTypeCodeTableRepo.findAll();
  }

  public Optional<AccessChannelCodeEntity> findAccessChannelCode(String accessChannelCode) {
    return Optional.ofNullable(loadAllAccessChannelCodes().get(accessChannelCode));
  }

  public Optional<IdentityTypeCodeEntity> findIdentityTypeCode(String identityTypeCode) {
    return Optional.ofNullable(loadAllIdentityTypeCodes().get(identityTypeCode));
  }

  /**
   * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
   *
   * @param typeValue typeValue path param
   * @param typeCode  typeCode path param
   * @return DigitalIDEntity if found.
   * @throws EntityNotFoundException if not
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
   * @param id path param id.
   * @return {@link DigitalIDEntity} if found.
   * @throws EntityNotFoundException if not found by the guid.
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
   * @param digitalID the digitalID entity object to be persisted.
   * @return the persisted object.
   */
  public DigitalIDEntity createDigitalID(DigitalIDEntity digitalID) {
    validateCreateParameters(digitalID);

    if (digitalID.getDigitalID() != null) {
      throw new InvalidParameterException(DIGITAL_ID_ATTRIBUTE);
    }
    digitalID.setUpdateDate(new Date());
    digitalID.setCreateDate(new Date());
    digitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode().toUpperCase());
    digitalID.setIdentityValue(digitalID.getIdentityValue().toUpperCase());

    return digitalIDRepository.save(digitalID);
  }

  /**
   * Updates a DigitalIDEntity
   *
   * @param digitalID the entity to be updated.
   * @return updated {@link DigitalIDEntity}
   * @throws EntityNotFoundException if not found by the {@link DigitalIDEntity#getDigitalID()}
   */
  public DigitalIDEntity updateDigitalID(DigitalIDEntity digitalID) {
    Optional<DigitalIDEntity> curDigitalID = digitalIDRepository.findById(digitalID.getDigitalID());
    if (curDigitalID.isPresent()) {
      DigitalIDEntity newDigitalID = curDigitalID.get();
      newDigitalID.setStudentID(digitalID.getStudentID());
      newDigitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode());
      newDigitalID.setIdentityValue(digitalID.getIdentityValue());
      newDigitalID.setLastAccessDate(digitalID.getLastAccessDate());
      newDigitalID.setLastAccessChannelCode(digitalID.getLastAccessChannelCode());
      newDigitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode().toUpperCase());
      newDigitalID.setIdentityValue(digitalID.getIdentityValue().toUpperCase());
      newDigitalID.setUpdateDate(digitalID.getUpdateDate());
      newDigitalID.setUpdateUser(digitalID.getUpdateUser());
      newDigitalID = digitalIDRepository.save(newDigitalID);
      return newDigitalID;
    } else {
      throw new EntityNotFoundException(DigitalIDEntity.class, DIGITAL_ID_ATTRIBUTE, digitalID.getDigitalID().toString());
    }
  }

  private Map<String, AccessChannelCodeEntity> loadAllAccessChannelCodes() {
    return getAccessChannelCodesList().stream().collect(Collectors.toMap(AccessChannelCodeEntity::getAccessChannelCode, accessChannel -> accessChannel));
  }


  private Map<String, IdentityTypeCodeEntity> loadAllIdentityTypeCodes() {
    return getIdentityTypeCodesList().stream().collect(Collectors.toMap(IdentityTypeCodeEntity::getIdentityTypeCode, identityTypeCodeEntity -> identityTypeCodeEntity));
  }
}
