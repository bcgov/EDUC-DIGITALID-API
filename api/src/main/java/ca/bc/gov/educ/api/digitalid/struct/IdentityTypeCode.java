package ca.bc.gov.educ.api.digitalid.struct;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import ca.bc.gov.educ.api.digitalid.struct.DigitalID.DigitalIDBuilder;

@Data
@Builder
public class IdentityTypeCode implements Serializable {
  String identityTypeCode;
  String label;
  String description;
  Integer displayOrder;
  Date effectiveDate;
  Date expiryDate;
}
