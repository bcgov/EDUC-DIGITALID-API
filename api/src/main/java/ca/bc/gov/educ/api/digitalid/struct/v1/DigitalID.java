package ca.bc.gov.educ.api.digitalid.struct.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigitalID {
  private String digitalID;
  private String studentID;
  @NotNull(message = "identityTypeCode cannot be null")
  @Size(max = 10)
  private String identityTypeCode;
  @NotNull(message = "identityValue cannot be null")
  @Size(max = 255)
  private String identityValue;
  @NotNull(message = "lastAccessDate cannot be null")
  private String lastAccessDate;
  @NotNull(message = "lastAccessChannelCode cannot be null")
  @Size(max = 10)
  private String lastAccessChannelCode;
  @Size(max = 32)
  private String createUser;
  @Null
  private String createDate;
  @Size(max = 32)
  private String updateUser;
  @Null
  private String updateDate;
}
