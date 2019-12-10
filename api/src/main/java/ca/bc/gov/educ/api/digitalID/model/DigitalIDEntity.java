package ca.bc.gov.educ.api.digitalID.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.UUID;

/**
 * Digital Identity Entity
 *
 * @author John Cox
 */

@Data
@Entity
@Table(name = "digital_identity")
public class DigitalIDEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @Column(name = "digital_identity_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
    UUID digitalID;

    @Column(name = "student_id", unique = true, columnDefinition = "BINARY(16)")
    UUID studentID;

    @NotNull(message="identityTypeCode cannot be null")
    @Column(name = "identity_type_code")
    String identityTypeCode;

    @NotNull(message="identityValue cannot be null")
    @Column(name = "identity_value")
    String identityValue;

    @PastOrPresent
    @NotNull(message="lastAccessDate cannot be null")
    @Column(name = "last_access_date")
    Date lastAccessDate;

    @NotNull(message="lastAccessChannelCode cannot be null")
    @Column(name = "last_access_channel_code")
    String lastAccessChannelCode;

    @Column(name = "create_user", updatable = false)
    String createUser;

    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    Date createDate;

    @Column(name = "update_user", updatable = false)
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date", updatable = false)
    Date updateDate;
}
