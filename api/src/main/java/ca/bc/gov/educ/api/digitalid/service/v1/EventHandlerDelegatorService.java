package ca.bc.gov.educ.api.digitalid.service.v1;

import ca.bc.gov.educ.api.digitalid.messaging.MessagePublisher;
import ca.bc.gov.educ.api.digitalid.struct.v1.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventHandlerDelegatorService {

  public static final String PAYLOAD_LOG = "Payload is :: ";
  public static final String RESPONDING_BACK = "responding back to NATS on {} channel ";
  private final EventHandlerService eventHandlerService;
  private final MessagePublisher messagePublisher;

  @Autowired
  public EventHandlerDelegatorService(final EventHandlerService eventHandlerService, final MessagePublisher messagePublisher) {
    this.eventHandlerService = eventHandlerService;
    this.messagePublisher = messagePublisher;
  }

  @Async("subscriberExecutor")
  public void handleEvent(final Event event) {
    final byte[] response;
    try {
      switch (event.getEventType()) {
        case UPDATE_DIGITAL_ID:
          log.info("received update digital id event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = this.eventHandlerService.handleUpdateDigitalIdEvent(event);
          log.info(RESPONDING_BACK, event.getReplyTo());
          this.messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case GET_DIGITAL_ID:
          log.info("received get digital id event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = this.eventHandlerService.handleGetDigitalIdEvent(event);
          log.info(RESPONDING_BACK, event.getReplyTo());
          this.messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case GET_DIGITAL_ID_LIST:
          log.info("received get digital id event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = this.eventHandlerService.handleGetDigitalIdListEvent(event);
          log.info(RESPONDING_BACK, event.getReplyTo());
          this.messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case UPDATE_DIGITAL_ID_LIST:
          log.info("received update digital id list event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = this.eventHandlerService.handleUpdateDigitalIdListEvent(event);
          log.info(RESPONDING_BACK, event.getReplyTo());
          this.messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        default:
          log.info("silently ignoring other events :: {}", event);
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

}
