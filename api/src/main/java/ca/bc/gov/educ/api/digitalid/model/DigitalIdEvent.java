package ca.bc.gov.educ.api.digitalid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "DIGITAL_ID_EVENT")
@Data
@DynamicUpdate
public class DigitalIdEvent {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "EVENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID eventId;

  @NotNull(message = "eventPayload cannot be null")
  @Column(name = "EVENT_PAYLOAD", length = 4000)
  private String eventPayload;

  @NotNull(message = "eventStatus cannot be null")
  @Column(name = "EVENT_STATUS", length = 50)
  private String eventStatus;
  @NotNull(message = "eventType cannot be null")
  @Column(name = "EVENT_TYPE", length = 100)
  private String eventType;
  @Column(name = "CREATE_USER", updatable = false)
  String createUser;
  @Column(name = "CREATE_DATE", updatable = false)
  @PastOrPresent
  LocalDateTime createDate;
  @Column(name = "UPDATE_USER")
  String updateUser;
  @Column(name = "UPDATE_DATE")
  @PastOrPresent
  LocalDateTime updateDate;
  @Column(name = "SAGA_ID", updatable = false)
  private UUID sagaId;
  @NotNull(message = "eventOutcome cannot be null.")
  @Column(name = "EVENT_OUTCOME", length = 100, nullable = false)
  private String eventOutcome;
  @Column(name = "REPLY_CHANNEL", length = 100)
  private String replyChannel;
}
