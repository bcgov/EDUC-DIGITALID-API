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
@Table(name = "digital_id_event")
@Data
@DynamicUpdate
public class DigitalIdEvent {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "event_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID eventId;

  @NotNull(message = "eventPayload cannot be null")
  @Column(name = "event_payload")
  private String eventPayload;

  @NotNull(message = "eventStatus cannot be null")
  @Column(name = "event_status")
  private String eventStatus;
  @NotNull(message = "eventType cannot be null")
  @Column(name = "event_type")
  private String eventType;
  @Column(name = "create_user", updatable = false)
  String createUser;
  @Column(name = "create_date", updatable = false)
  @PastOrPresent
  LocalDateTime createDate;
  @Column(name = "update_user")
  String updateUser;
  @Column(name = "update_date")
  @PastOrPresent
  LocalDateTime updateDate;
  @Column(name = "saga_id", updatable = false)
  private UUID sagaId;
  @NotNull(message = "eventOutcome cannot be null.")
  @Column(name = "event_outcome")
  private String eventOutcome;
  @Column(name = "reply_channel")
  private String replyChannel;
}
