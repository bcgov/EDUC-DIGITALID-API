package ca.bc.gov.educ.api.digitalid.messaging;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.Optional;

/**
 * The type Message pub sub.
 */
@Slf4j
@SuppressWarnings("java:S2142")
public abstract class MessagePubSub implements Closeable {
  /**
   * The Connection.
   */
  protected Connection connection;

  /**
   * Close.
   */
  @Override
  public void close() {
    if(Optional.ofNullable(connection).isPresent()){
      log.info("closing nats connection...");
      try {
        connection.close();
      } catch (InterruptedException e) {
        log.error("error while closing nats connection ...", e);
      }
      log.info("nats connection closed...");
    }
  }

}
