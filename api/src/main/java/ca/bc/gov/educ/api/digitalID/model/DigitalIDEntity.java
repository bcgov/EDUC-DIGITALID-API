package ca.bc.gov.educ.api.digitalID.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

/**
 * Digital Identity Entity
 *
 * @author John Cox
 */

@Data
public class DigitalIDEntity {
    @JsonAlias({"digital_identity_id", "digitalId"})
    Integer digitalID;
    @JsonAlias({"student_id", "studentId"})
    Integer studentID;
    @NotNull(message="identityTypeCode cannot be null")
    @JsonAlias({"identity_type_code", "identityTypeCode"})
    String identityTypeCode;
    @NotNull(message="identityValue cannot be null")
    @JsonAlias({"identity_value", "identityValue"})
    String identityValue;
    @NotNull(message="lastAccessTime cannot be null")
    @JsonAlias({"last_access_time", "lastAccessTime"})
    @PastOrPresent
    Date lastAccessDate;
    @NotNull(message="lastAccessChannelCode cannot be null")
    @JsonAlias({"last_access_channel_code", "lastAccessChannelCode"})
    String lastAccessChannelCode;
    @JsonAlias({"create_user", "createUser"})
    String createUser;
    @JsonAlias({"create_date", "createDate"})
    @PastOrPresent
    Date createDate;
    @JsonAlias({"update_user", "updateUser"})
    String updateUser;
    @JsonAlias({"update_date", "updateDate"})
    @PastOrPresent
    Date updateDate;
}
