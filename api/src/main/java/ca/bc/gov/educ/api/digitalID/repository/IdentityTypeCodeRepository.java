package ca.bc.gov.educ.api.digitalID.repository;

import ca.bc.gov.educ.api.digitalID.model.IdentityTypeCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * IdentityTypeCodeRepository
 *
 * @author John Cox
 */

@Repository
public interface IdentityTypeCodeRepository extends CrudRepository<IdentityTypeCodeEntity, String> {
}
