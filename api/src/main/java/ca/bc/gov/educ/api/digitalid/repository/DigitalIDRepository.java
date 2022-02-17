package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

  List<DigitalIDEntity> findAllByStudentID(UUID studentID);
}
