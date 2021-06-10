package ca.bc.gov.educ.api.digitalid.messaging;

import ca.bc.gov.educ.api.digitalid.helpers.LogHelper;
import ca.bc.gov.educ.api.digitalid.service.v1.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.digitalid.struct.v1.Event;
import ca.bc.gov.educ.api.digitalid.utils.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static ca.bc.gov.educ.api.digitalid.constants.Topics.DIGITAL_ID_API_TOPIC;


@Component
@Slf4j
public class MessageSubscriber {

  private final EventHandlerDelegatorService eventHandlerDelegatorService;
  private final Connection connection;

  @Autowired
  public MessageSubscriber(final Connection con, final EventHandlerDelegatorService eventHandlerDelegatorService) {
    this.eventHandlerDelegatorService = eventHandlerDelegatorService;
    this.connection = con;
  }

  /**
   * This subscription will makes sure the messages are required to acknowledge manually to STAN.
   * Subscribe.
   */
  @PostConstruct
  public void subscribe() {
    final String queue = DIGITAL_ID_API_TOPIC.toString().replace("_", "-");
    final var dispatcher = this.connection.createDispatcher(this.onMessage());
    dispatcher.subscribe(DIGITAL_ID_API_TOPIC.toString(), queue);
  }

  /**
   * On message message handler.
   *
   * @return the message handler
   */
  private MessageHandler onMessage() {
    return (Message message) -> {
      if (message != null) {
        try {
          final var eventString = new String(message.getData());
          LogHelper.logMessagingEventDetails(eventString);
          final var event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
          this.eventHandlerDelegatorService.handleEvent(event);
        } catch (final Exception e) {
          log.error("Exception ", e);
        }
      }
    };
  }


}
