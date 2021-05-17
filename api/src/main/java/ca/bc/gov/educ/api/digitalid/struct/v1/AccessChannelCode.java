package ca.bc.gov.educ.api.digitalid.struct.v1;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AccessChannelCode implements Serializable {
  private static final long serialVersionUID = -4574890155916637255L;
  String accessChannelCode;
  String label;
  String description;
  Integer displayOrder;
  String effectiveDate;
  String expiryDate;
}
