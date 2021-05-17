package ca.bc.gov.educ.api.digitalid.service.v1;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.model.v1.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
  public DigitalIDService(final DigitalIDRepository digitalIDRepository, final AccessChannelCodeTableRepository accessChannelCodeTableRepo, final IdentityTypeCodeTableRepository identityTypeCodeTableRepo) {
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
    return this.accessChannelCodeTableRepo.findAll();
  }

  /**
   * Returns the full list of access channel codes
   *
   * @return {@link List<IdentityTypeCodeEntity>}
   */
  @Cacheable("identityTypeCodes")
  public List<IdentityTypeCodeEntity> getIdentityTypeCodesList() {
    return this.identityTypeCodeTableRepo.findAll();
  }

  public Optional<AccessChannelCodeEntity> findAccessChannelCode(final String accessChannelCode) {
    return Optional.ofNullable(this.loadAllAccessChannelCodes().get(accessChannelCode));
  }

  public Optional<IdentityTypeCodeEntity> findIdentityTypeCode(final String identityTypeCode) {
    return Optional.ofNullable(this.loadAllIdentityTypeCodes().get(identityTypeCode));
  }

  /**
   * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
   *
   * @param typeValue typeValue path param
   * @param typeCode  typeCode path param
   * @return DigitalIDEntity if found.
   * @throws EntityNotFoundException if not
   */
  public DigitalIDEntity searchDigitalId(final String typeCode, final String typeValue) {

    final Optional<DigitalIDEntity> result = this.getDigitalIDRepository().findByIdentityTypeCodeAndIdentityValue(typeCode.toUpperCase(), typeValue.toUpperCase());

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
  public DigitalIDEntity retrieveDigitalID(final UUID id) {
    final Optional<DigitalIDEntity> result = this.getDigitalIDRepository().findById(id);
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
  public DigitalIDEntity createDigitalID(final DigitalIDEntity digitalID) {
    return this.digitalIDRepository.save(digitalID);
  }

  /**
   * Updates a DigitalIDEntity
   *
   * @param digitalID the entity to be updated.
   * @param id        the PK
   * @return updated {@link DigitalIDEntity}
   * @throws EntityNotFoundException if not found by the {@link DigitalIDEntity#getDigitalID()}
   */
  public DigitalIDEntity updateDigitalID(final DigitalIDEntity digitalID, final UUID id) {
    final Optional<DigitalIDEntity> curDigitalID = this.digitalIDRepository.findById(id);
    if (curDigitalID.isPresent()) {
      final DigitalIDEntity newDigitalID = curDigitalID.get();
      BeanUtils.copyProperties(digitalID, newDigitalID, "createUser", "createDate");
      newDigitalID.setUpdateDate(LocalDateTime.now());
      return this.digitalIDRepository.save(newDigitalID);
    } else {
      throw new EntityNotFoundException(DigitalIDEntity.class, DIGITAL_ID_ATTRIBUTE, id.toString());
    }
  }

  private Map<String, AccessChannelCodeEntity> loadAllAccessChannelCodes() {
    return this.getAccessChannelCodesList().stream().collect(Collectors.toMap(AccessChannelCodeEntity::getAccessChannelCode, accessChannel -> accessChannel));
  }


  private Map<String, IdentityTypeCodeEntity> loadAllIdentityTypeCodes() {
    return this.getIdentityTypeCodesList().stream().collect(Collectors.toMap(IdentityTypeCodeEntity::getIdentityTypeCode, identityTypeCodeEntity -> identityTypeCodeEntity));
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteAll() {
    this.getDigitalIDRepository().deleteAll();
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteById(final UUID id) {
    val entityOptional = this.digitalIDRepository.findById(id);
    val entity = entityOptional.orElseThrow(() -> new EntityNotFoundException(DigitalIDEntity.class, DIGITAL_ID_ATTRIBUTE, id.toString()));
    this.digitalIDRepository.delete(entity);
  }
}
