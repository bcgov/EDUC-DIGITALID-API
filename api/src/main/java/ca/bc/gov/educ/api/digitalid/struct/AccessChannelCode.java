package ca.bc.gov.educ.api.digitalid.struct;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AccessChannelCode implements Serializable {
  String accessChannelCode;
  String label;
  String description;
  Integer displayOrder;
  Date effectiveDate;
  Date expiryDate;
}
