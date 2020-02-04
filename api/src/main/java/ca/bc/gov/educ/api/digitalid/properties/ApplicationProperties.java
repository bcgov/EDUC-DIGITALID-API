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

  @Value("${client.id}")
  private String clientID;
  @Value("${client.secret}")
  private String clientSecret;
  @Value("${token.url}")
  private String tokenURL;
  @Value("${codetable.api.url}")
  private String codeTableApiURL;

}
