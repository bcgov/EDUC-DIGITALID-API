package ca.bc.gov.educ.api.digitalID.repository;

import ca.bc.gov.educ.api.digitalID.model.AccessChannelCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * AccessChannelCodeRepository
 *
 * @author John Cox
 */

@Repository
public interface AccessChannelCodeRepository extends CrudRepository<AccessChannelCodeEntity, String> {
}
