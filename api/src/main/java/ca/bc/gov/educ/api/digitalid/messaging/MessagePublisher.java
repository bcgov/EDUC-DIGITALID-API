package ca.bc.gov.educ.api.digitalid.messaging;

import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.service.EventHandlerService;
import io.nats.streaming.AckHandler;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@SuppressWarnings("java:S2142")
public class MessagePublisher implements Closeable {
  private final ExecutorService executorService = Executors.newFixedThreadPool(2);
  private StreamingConnection connection;
  private final StreamingConnectionFactory connectionFactory;

  @Getter(PRIVATE)
  private final EventHandlerService eventHandlerService;

  @Autowired
  public MessagePublisher(final ApplicationProperties applicationProperties, final EventHandlerService eventHandlerService) throws IOException, InterruptedException {
    this.eventHandlerService = eventHandlerService;
    Options options = new Options.Builder().maxPingsOut(100)
            .natsUrl(applicationProperties.getNatsUrl())
            .clusterId(applicationProperties.getNatsClusterId())
            .connectionLostHandler(this::connectionLostHandler)
            .clientId("digital-id-api-publisher" + UUID.randomUUID().toString()).build();
    connectionFactory = new StreamingConnectionFactory(options);
    connection = connectionFactory.createConnection();
  }

  public void dispatchMessage(String subject, byte[] message) throws InterruptedException, TimeoutException, IOException {
    try {
      AckHandler ackHandler = getAckHandler();
      connection.publish(subject, message, ackHandler);
    } catch (IllegalStateException e) {
      log.error(e.getMessage());
      if (e.getMessage() != null && e.getMessage().contains("stan: connection closed")) {
        executorService.execute(() -> {
          try {
            this.connectionLostHandler(connection, e);
            dispatchMessage(subject, message);
          } catch (InterruptedException | TimeoutException | IOException exception) {
            log.error("exception occurred :: ", exception);
          }
        });
      }

    }

  }


  private AckHandler getAckHandler() {
    return new AckHandler() {
      @Override
      public void onAck(String guid, Exception err) {
        log.trace("already handled.");
      }

      @Override
      public void onAck(String guid, String subject, byte[] data, Exception ex) {

        if (ex != null) {
          executorService.execute(() -> {
            try {
              retryPublish(subject, data);
            } catch (InterruptedException | TimeoutException | IOException e) {
              log.error("Exception", e);
            }
          });

        } else {
          log.trace("acknowledgement received {}", guid);
        }
      }
    };
  }

  public void retryPublish(String subject, byte[] message) throws InterruptedException, TimeoutException, IOException {
    log.trace("retrying...");
    this.dispatchMessage(subject, message);
  }

  /**
   * This method will keep retrying for a connection.
   */
  private void connectionLostHandler(StreamingConnection streamingConnection, Exception e) {
    if (e != null) {
      int numOfRetries = 1;
      while (true) {
        try {
          log.trace("retrying connection as connection was lost :: retrying ::" + numOfRetries++);
          connection = connectionFactory.createConnection();
          break;
        } catch (IOException | InterruptedException ex) {
          log.error("exception occurred", ex);
          try {
            double sleepTime = (2 * numOfRetries);
            TimeUnit.SECONDS.sleep((long) sleepTime);
          } catch (InterruptedException exc) {
            log.error("exception occurred", exc);
          }

        }
      }
    }
  }


  @Override
  public void close() {
    if (!executorService.isShutdown()) {
      executorService.shutdown();
    }
  }
}
