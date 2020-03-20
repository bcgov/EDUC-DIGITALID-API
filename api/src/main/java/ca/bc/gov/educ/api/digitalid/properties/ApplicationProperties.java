package ca.bc.gov.educ.api.digitalid.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class holds all application properties
 *
 * @author Marco Villeneuve
 */
@Component
@Getter
@Setter
public class ApplicationProperties {
  public static final String API_NAME = "DIGITAL_ID_API";

  @Value("${nats.streaming.server.url}")
  @Getter
  private String natsUrl;

  @Value("${nats.streaming.server.clusterId}")
  @Getter
  private String natsClusterId;

  @Value("${nats.streaming.server.clientId}")
  @Getter
  private String natsClientId;
}
