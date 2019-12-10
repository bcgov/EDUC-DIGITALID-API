package ca.bc.gov.educ.api.digitalID.repository;

import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * DigitalIDRepository
 *
 * @author John Cox
 */

@Repository
public interface DigitalIDRepository extends CrudRepository<DigitalIDEntity, UUID> {
    Optional<DigitalIDEntity> findByIdentityTypeCodeAndIdentityValue(String identityTypeCode, String identityValue);
    Optional<DigitalIDEntity> findByDigitalID(UUID id);
}
