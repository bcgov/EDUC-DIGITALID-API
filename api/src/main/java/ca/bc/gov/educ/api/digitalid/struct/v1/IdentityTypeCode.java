package ca.bc.gov.educ.api.digitalid.struct.v1;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class IdentityTypeCode implements Serializable {
  private static final long serialVersionUID = -4159285091218911030L;
  String identityTypeCode;
  String label;
  String description;
  Integer displayOrder;
  String effectiveDate;
  String expiryDate;
}
