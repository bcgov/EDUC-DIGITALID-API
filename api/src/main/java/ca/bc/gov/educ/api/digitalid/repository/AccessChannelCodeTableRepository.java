package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.AccessChannelCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Access Channel Code Table Repository
 *
 * @author Marco Villeneuve
 *
 */
@Repository
public interface AccessChannelCodeTableRepository extends CrudRepository<AccessChannelCodeEntity, String> {
    @Override
    List<AccessChannelCodeEntity> findAll();
}
