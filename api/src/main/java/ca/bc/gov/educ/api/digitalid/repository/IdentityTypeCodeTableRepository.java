package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.IdentityTypeCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Identity Type Code Table Repository
 *
 * @author Marco Villeneuve
 *
 */
@Repository
public interface IdentityTypeCodeTableRepository extends CrudRepository<IdentityTypeCodeEntity, String> {
    @Override
    List<IdentityTypeCodeEntity> findAll();
}
