package ca.bc.gov.educ.api.digitalid.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("squid:S1700")
public class DigitalID {
    private String digitalID;
    private String studentID;
    @NotNull(message = "identityTypeCode cannot be null")
    private String identityTypeCode;
    @NotNull(message = "identityValue cannot be null")
    private String identityValue;
    @PastOrPresent
    @NotNull(message = "lastAccessDate cannot be null")
    private Date lastAccessDate;
    @NotNull(message = "lastAccessChannelCode cannot be null")
    private String lastAccessChannelCode;
    private String createUser;
    @Null
    private Date createDate;
    private String updateUser;
    @Null
    private Date updateDate;
}
