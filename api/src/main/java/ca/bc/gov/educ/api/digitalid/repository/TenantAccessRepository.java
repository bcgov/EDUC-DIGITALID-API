package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.TenantAccessEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantAccessRepository extends CrudRepository<TenantAccessEntity, UUID> {
  Optional<TenantAccessEntity> findByClientIDAndTenantID(String clientID, String tenantID);

}
