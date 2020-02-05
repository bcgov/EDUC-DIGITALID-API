package ca.bc.gov.educ.api.digitalid.struct;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@SuppressWarnings("squid:S1700")
public class AccessChannelCode implements Serializable {
  String accessChannelCode;
  String label;
  String description;
  Integer displayOrder;
  Date effectiveDate;
  Date expiryDate;
}
