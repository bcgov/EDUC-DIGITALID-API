package ca.bc.gov.educ.api.digitalid.model.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Digital Identity Entity
 *
 * @author John Cox
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "digital_identity")
@Data
@DynamicUpdate
public class DigitalIDEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "digital_identity_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID digitalID;

  @Column(name = "student_id", unique = true, columnDefinition = "BINARY(16)")
  UUID studentID;

  @Column(name = "identity_type_code", length = 10)
  String identityTypeCode;

  @Column(name = "identity_value")
  String identityValue;

  @Column(name = "last_access_date")
  LocalDateTime lastAccessDate;

  @Column(name = "last_access_channel_code", length = 10)
  String lastAccessChannelCode;

  @Column(name = "auto_matched_date")
  LocalDateTime autoMatchedDate;

  @Column(name = "create_user", updatable = false, length = 32)
  String createUser;

  @PastOrPresent
  @Column(name = "create_date", updatable = false)
  LocalDateTime createDate;

  @Column(name = "update_user", length = 32)
  String updateUser;

  @PastOrPresent
  @Column(name = "update_date")
  LocalDateTime updateDate;

}
