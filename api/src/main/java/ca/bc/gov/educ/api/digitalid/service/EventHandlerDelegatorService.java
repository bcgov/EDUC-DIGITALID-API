package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.messaging.MessagePublisher;
import ca.bc.gov.educ.api.digitalid.struct.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventHandlerDelegatorService {

  public static final String PAYLOAD_LOG = "Payload is :: ";
  private final EventHandlerService eventHandlerService;
  private final MessagePublisher messagePublisher;

  @Autowired
  public EventHandlerDelegatorService(EventHandlerService eventHandlerService, MessagePublisher messagePublisher) {
    this.eventHandlerService = eventHandlerService;
    this.messagePublisher = messagePublisher;
  }

  @Async("subscriberExecutor")
  public void handleEvent(Event event) {
    byte[] response;
    try {
      switch (event.getEventType()) {
        case UPDATE_DIGITAL_ID:
          log.info("received update digital id event :: ");
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = eventHandlerService.handleUpdateDigitalIdEvent(event);
          messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case GET_DIGITAL_ID:
          log.info("received get digital id event :: ");
          log.trace(PAYLOAD_LOG + event.getEventPayload());
          response = eventHandlerService.handleGetDigitalIdEvent(event);
          messagePublisher.dispatchMessage(event.getReplyTo(), response);
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
