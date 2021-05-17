package ca.bc.gov.educ.api.digitalid.validator;

import ca.bc.gov.educ.api.digitalid.model.v1.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.service.v1.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DigitalIDPayloadValidator {

  public static final String IDENTITY_TYPE_CODE = "identityTypeCode";
  public static final String LAST_ACCESS_CHANNEL_CODE = "lastAccessChannelCode";
  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDService digitalIDService;

  @Autowired
  public DigitalIDPayloadValidator(final DigitalIDService digitalIDService) {
    this.digitalIDService = digitalIDService;
  }

  public List<FieldError> validatePayload(final DigitalID digitalID, final boolean isCreateOperation) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (isCreateOperation && digitalID.getDigitalID() != null) {
      apiValidationErrors.add(this.createFieldError(digitalID.getDigitalID(), "digitalID should be null for post operation.", "digitalID"));
    }
    if (LocalDateTime.now().isBefore(LocalDateTime.parse(digitalID.getLastAccessDate()))) {
      apiValidationErrors.add(this.createFieldError(digitalID.getLastAccessDate(), "Last Access Date should be past or present", "lastAccessDate"));
    }
    this.validateIdentityTypeCode(digitalID, apiValidationErrors);
    this.validateLastAccessChannelCode(digitalID, apiValidationErrors);
    return apiValidationErrors;
  }

  protected void validateIdentityTypeCode(final DigitalID digitalID, final List<FieldError> apiValidationErrors) {
    final Optional<IdentityTypeCodeEntity> identityTypeCodeEntity = this.digitalIDService.findIdentityTypeCode(digitalID.getIdentityTypeCode());
    if (!identityTypeCodeEntity.isPresent()) {
      apiValidationErrors.add(this.createFieldError(digitalID.getIdentityTypeCode(), "Invalid Identity Type Code.", IDENTITY_TYPE_CODE));
    } else if (identityTypeCodeEntity.get().getEffectiveDate() != null && identityTypeCodeEntity.get().getEffectiveDate().isAfter(LocalDateTime.now())) {
      apiValidationErrors.add(this.createFieldError(digitalID.getIdentityTypeCode(), "Identity Type Code provided is not yet effective.", IDENTITY_TYPE_CODE));
    } else if (identityTypeCodeEntity.get().getExpiryDate() != null && identityTypeCodeEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
      apiValidationErrors.add(this.createFieldError(digitalID.getIdentityTypeCode(), "Identity Type Code provided has expired.", IDENTITY_TYPE_CODE));
    }
  }

  protected void validateLastAccessChannelCode(final DigitalID digitalID, final List<FieldError> apiValidationErrors) {
    final Optional<AccessChannelCodeEntity> accessChannelCodeEntity = this.digitalIDService.findAccessChannelCode(digitalID.getLastAccessChannelCode());
    if (!accessChannelCodeEntity.isPresent()) {
      apiValidationErrors.add(this.createFieldError(digitalID.getLastAccessChannelCode(), "Invalid Last Access Channel Code.", LAST_ACCESS_CHANNEL_CODE));
    } else if (accessChannelCodeEntity.get().getEffectiveDate() != null && accessChannelCodeEntity.get().getEffectiveDate().isAfter(LocalDateTime.now())) {
      apiValidationErrors.add(this.createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided is not yet effective.", LAST_ACCESS_CHANNEL_CODE));
    } else if (accessChannelCodeEntity.get().getExpiryDate() != null && accessChannelCodeEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
      apiValidationErrors.add(this.createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided has expired.", LAST_ACCESS_CHANNEL_CODE));
    }
  }

  private FieldError createFieldError(final Object rejectedValue, final String message, final String fieldName) {
    return new FieldError("DigitalID", fieldName, rejectedValue, false, null, null, message);
  }

}
