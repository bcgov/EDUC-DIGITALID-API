package ca.bc.gov.educ.api.digitalid.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;

/**
 * Access Channel Code Table Repository
 *
 * @author Marco Villeneuve
 * 
 */
@Repository
public interface AccessChannelCodeTableRepository extends CrudRepository<AccessChannelCodeEntity, Long> {
    List<AccessChannelCodeEntity> findAll();
}
