package ca.bc.gov.educ.api.digitalid.messaging;

import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;

/**
 * The type Nats connection.
 */
@Component
@Slf4j
public class NatsConnection implements Closeable {
  @Getter
  private final Connection natsCon;

  /**
   * Instantiates a new Nats connection.
   *
   * @param applicationProperties the application properties
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  @Autowired
  public NatsConnection(final ApplicationProperties applicationProperties) throws IOException, InterruptedException {
    this.natsCon = connectToNats(applicationProperties.getServer(), applicationProperties.getMaxReconnect(), applicationProperties.getConnectionName());
  }

  private Connection connectToNats(String serverUrl, int maxReconnect, String connectionName) throws IOException, InterruptedException {
    io.nats.client.Options natsOptions = new io.nats.client.Options.Builder()
        .connectionListener(this::connectionListener)
        .maxPingsOut(5)
        .pingInterval(Duration.ofSeconds(2))
        .connectionName(connectionName)
        .connectionTimeout(Duration.ofSeconds(5))
        .executor(new EnhancedQueueExecutor.Builder()
            .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("core-nats-%d").build())
            .setCorePoolSize(10).setMaximumPoolSize(10).setKeepAliveTime(Duration.ofSeconds(60)).build())
        .maxReconnects(maxReconnect)
        .reconnectWait(Duration.ofSeconds(2))
        .servers(new String[]{serverUrl})
        .build();
    return Nats.connect(natsOptions);
  }

  private void connectionListener(Connection connection, ConnectionListener.Events events) {
    log.info("NATS -> {}", events.toString());
  }


  @Override
  public void close() {
    if (natsCon != null) {
      log.info("closing nats connection...");
      try {
        natsCon.close();
      } catch (InterruptedException e) {
        log.error("error while closing nats connection...", e);
        Thread.currentThread().interrupt();
      }
      log.info("nats connection closed...");
    }
  }

  @Bean
  public Connection getConnection() {
    return natsCon;
  }
}
