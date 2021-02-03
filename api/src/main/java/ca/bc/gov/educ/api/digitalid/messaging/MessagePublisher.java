package ca.bc.gov.educ.api.digitalid.messaging;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Message publisher.
 */
@Component
@Slf4j
public class MessagePublisher {

  private final Connection connection;

  @Autowired
  public MessagePublisher(final Connection con) {
    this.connection = con;
  }

  /**
   * Dispatch message.
   *
   * @param subject the subject
   * @param message the message
   */
  public void dispatchMessage(String subject, byte[] message) {
      connection.publish(subject, message);
  }
}
