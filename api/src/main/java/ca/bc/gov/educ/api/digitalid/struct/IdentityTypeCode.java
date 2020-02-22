package ca.bc.gov.educ.api.digitalid.struct;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@SuppressWarnings("squid:S1700")
public class IdentityTypeCode implements Serializable {
  String identityTypeCode;
  String label;
  String description;
  Integer displayOrder;
  String effectiveDate;
  String expiryDate;
}
