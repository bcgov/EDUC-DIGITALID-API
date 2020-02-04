package ca.bc.gov.educ.api.digitalid.validator;

import ca.bc.gov.educ.api.digitalid.service.CodeTableService;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DigitalIDPayloadValidator {

  @Getter(AccessLevel.PRIVATE)
  private final CodeTableService codeTableService;

  @Autowired
  public DigitalIDPayloadValidator(final CodeTableService codeTableService) {
    this.codeTableService = codeTableService;
  }

  public List<FieldError> validatePayload(final DigitalID digitalID) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    validateLastAccessChannelCode(digitalID, apiValidationErrors);
    return apiValidationErrors;
  }

  protected void validateLastAccessChannelCode(DigitalID digitalID, List<FieldError> apiValidationErrors) {
    final AccessChannelCode accessChannelCode = getCodeTableService().findAccessChannelCode(digitalID.getLastAccessChannelCode());
    if (accessChannelCode == null) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Invalid Last Access Channel Code."));
    } else if (accessChannelCode.getEffectiveDate() != null && new Date().before(accessChannelCode.getEffectiveDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided is not yet effective."));
    } else if (accessChannelCode.getExpiryDate() != null && new Date().after(accessChannelCode.getExpiryDate())) {
      apiValidationErrors.add(createFieldError(digitalID.getLastAccessChannelCode(), "Last Access Channel Code provided has expired."));
    }
  }

  private FieldError createFieldError(Object rejectedValue, String message) {
    return new FieldError("DigitalID", "lastAccessChannelCode", rejectedValue, false, null, null, message);
  }

}
