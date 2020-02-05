package ca.bc.gov.educ.api.digitalid.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;

/**
 * Identity Type Code Table Repository
 *
 * @author Marco Villeneuve
 * 
 */
@Repository
public interface IdentityTypeCodeTableRepository extends CrudRepository<IdentityTypeCodeEntity, String> {
    List<IdentityTypeCodeEntity> findAll();
}
