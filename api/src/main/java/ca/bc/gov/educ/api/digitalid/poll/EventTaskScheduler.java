package ca.bc.gov.educ.api.digitalid.poll;

import ca.bc.gov.educ.api.digitalid.constants.EventOutcome;
import ca.bc.gov.educ.api.digitalid.constants.EventType;
import ca.bc.gov.educ.api.digitalid.messaging.MessagePublisher;
import ca.bc.gov.educ.api.digitalid.model.DigitalIdEvent;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIdEventRepository;
import ca.bc.gov.educ.api.digitalid.struct.Event;
import ca.bc.gov.educ.api.digitalid.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.digitalid.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.digitalid.constants.EventType.DIGITAL_ID_EVENT_OUTBOX_PROCESSED;
import static ca.bc.gov.educ.api.digitalid.constants.Topics.DIGITAL_ID_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {

  @Getter(PRIVATE)
  private final MessagePublisher messagePubSub;
  @Getter(PRIVATE)
  private final DigitalIdEventRepository digitalIdEventRepository;

  @Autowired
  public EventTaskScheduler(MessagePublisher messagePubSub, DigitalIdEventRepository digitalIdEventRepository) {
    this.messagePubSub = messagePubSub;
    this.digitalIdEventRepository = digitalIdEventRepository;
  }

  @Scheduled(cron = "0/1 * * * * *")
  @SchedulerLock(name = "EventTablePoller",
          lockAtLeastFor = "900ms", lockAtMostFor = "950ms")
  public void pollEventTableAndPublish() throws InterruptedException, IOException, TimeoutException {
    List<DigitalIdEvent> events = getDigitalIdEventRepository().findByEventStatus(DB_COMMITTED.toString());
    if (!events.isEmpty()) {
      for (DigitalIdEvent event : events) {
        try {
          if (event.getReplyChannel() != null) {
            getMessagePubSub().dispatchMessage(event.getReplyChannel(), digitalIdEventProcessed(event));
          }
          getMessagePubSub().dispatchMessage(DIGITAL_ID_API_TOPIC.toString(), createOutboxEvent(event));
        } catch (InterruptedException | TimeoutException | IOException e) {
          log.error("exception occurred", e);
          throw e;
        }
      }
    } else {
      log.trace("no unprocessed records.");
    }
  }

  private byte[] digitalIdEventProcessed(DigitalIdEvent digitalIdEvent) throws JsonProcessingException {
    Event event = Event.builder()
            .sagaId(digitalIdEvent.getSagaId())
            .eventType(EventType.valueOf(digitalIdEvent.getEventType()))
            .eventOutcome(EventOutcome.valueOf(digitalIdEvent.getEventOutcome()))
            .eventPayload(digitalIdEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }

  private byte[] createOutboxEvent(DigitalIdEvent digitalIdEvent) throws JsonProcessingException {
    Event event = Event.builder().eventType(DIGITAL_ID_EVENT_OUTBOX_PROCESSED).eventPayload(digitalIdEvent.getEventId().toString()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}
