package ca.bc.gov.educ.api.digitalid.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;

/**
 * DigitalIDRepository
 *
 * @author John Cox
 */

@Repository
public interface DigitalIDRepository extends CrudRepository<DigitalIDEntity, UUID> {
    Optional<DigitalIDEntity> findByIdentityTypeCodeAndIdentityValue(String identityTypeCode, String identityValue);
}
