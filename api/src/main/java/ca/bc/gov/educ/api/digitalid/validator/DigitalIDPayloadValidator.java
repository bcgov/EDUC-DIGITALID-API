package ca.bc.gov.educ.api.digitalid.validator;

import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.IdentityTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class DigitalIDPayloadValidator {

  public static final String IDENTITY_TYPE_CODE = "identityTypeCode";
  public static final String LAST_ACCESS_CHANNEL_CODE = "lastAccessChannelCode";
  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDService digitalIDService;
  private static final DigitalIDMapper mapper = DigitalIDMapper.mapper;

  @Autowired
  public DigitalIDPayloadValidator(final DigitalIDService digitalIDService) {
    this.digitalIDService = digitalIDService;
  }

  public List<FieldError> validatePayload(final DigitalID digitalID, boolean isCreateOperation) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (isCreateOperation && digitalID.getDigitalID() != null) {
      apiValidationErrors.add(createFieldError(digitalID.getDigitalID(), "digitalID should be null for post operation.", "digitalID"));
    }
    validateIdentityTypeCode(digitalID, apiValidationErrors);
    validateLastAccessChannelCode(digitalID, apiValidationErrors);
    return apiValidationErrors;
  }

  protected void validateIdentityTypeCode(DigitalID digitalID, List<FieldError> apiValidationErrors) {
    Optional<IdentityTypeCodeEntity> identityTypeCodeEntity = digitalIDService.findIdentityTypeCode(digitalID.getIdentityTypeCode());
    final IdentityTypeCode identityTypeCode = identityTypeCodeEntity.map(mapper::toStructure).orElse(null);
    if (identityTypeCode == null) {
      apiValidationErrors.add(createFieldError(digitalID.getIdentityTypeCode(), "Invalid Identity Type Code.", IDENTITY_TYPE_CODE));
    } else if (identityTypeCode.getEffectiveDate() != null && new Date().before(identityTypeCode.getEffectiveDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getIdentityTypeCode(), "Identity Type Code provided is not yet effective.", IDENTITY_TYPE_CODE));
    } else if (identityTypeCode.getExpiryDate() != null && new Date().after(identityTypeCode.getExpiryDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getIdentityTypeCode(), "Identity Type Code provided has expired.", IDENTITY_TYPE_CODE));
    }
  }

  protected void validateLastAccessChannelCode(DigitalID digitalID, List<FieldError> apiValidationErrors) {
    Optional<AccessChannelCodeEntity> accessChannelCodeEntity = digitalIDService.findAccessChannelCode(digitalID.getLastAccessChannelCode());
    final AccessChannelCode accessChannelCode = accessChannelCodeEntity.map(mapper::toStructure).orElse(null);
    if (accessChannelCode == null) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Invalid Last Access Channel Code.", LAST_ACCESS_CHANNEL_CODE));
    } else if (accessChannelCode.getEffectiveDate() != null && new Date().before(accessChannelCode.getEffectiveDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided is not yet effective.", LAST_ACCESS_CHANNEL_CODE));
    } else if (accessChannelCode.getExpiryDate() != null && new Date().after(accessChannelCode.getExpiryDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided has expired.", LAST_ACCESS_CHANNEL_CODE));
    }
  }

  private FieldError createFieldError(Object rejectedValue, String message, String fieldName) {
    return new FieldError("DigitalID", fieldName, rejectedValue, false, null, null, message);
  }

}
