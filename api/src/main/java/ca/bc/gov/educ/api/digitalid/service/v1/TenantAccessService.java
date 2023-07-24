package ca.bc.gov.educ.api.digitalid.service.v1;

import ca.bc.gov.educ.api.digitalid.model.v1.TenantAccessEntity;
import ca.bc.gov.educ.api.digitalid.repository.TenantAccessRepository;
import ca.bc.gov.educ.api.digitalid.struct.v1.TenantAccess;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TenantAccessService {

  @Getter(AccessLevel.PRIVATE)
  private final TenantAccessRepository tenantAccessRepository;

  @Autowired
  public TenantAccessService(TenantAccessRepository tenantAccessRepository) {
    this.tenantAccessRepository = tenantAccessRepository;
  }

  public TenantAccess searchTenantAccess(final String clientID, final String tenantID) {
    final Optional<TenantAccessEntity> result = tenantAccessRepository.findByClientIDAndTenantID(clientID.toUpperCase(), tenantID.toUpperCase());

    TenantAccess access = new TenantAccess();
    access.setClientID(clientID);
    access.setTenantID(tenantID);

    if (result.isPresent()) {
      access.setIsValid("true");
    } else {
      access.setIsValid("false");
    }
    return access;
  }

}
