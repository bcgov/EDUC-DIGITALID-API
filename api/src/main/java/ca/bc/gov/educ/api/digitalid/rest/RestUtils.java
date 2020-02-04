package ca.bc.gov.educ.api.digitalid.rest;

import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
@Component
public class RestUtils {


  private static Logger logger = Logger.getLogger(RestUtils.class);

  private final ApplicationProperties props;

  public RestUtils(@Autowired final ApplicationProperties props) {
    this.props = props;
  }

  public RestTemplate getRestTemplate() {
    return getRestTemplate(null);
  }

  public RestTemplate getRestTemplate(List<String> scopes) {
    logger.debug("Calling get token method");
    ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
    resourceDetails.setClientId(props.getClientID());
    resourceDetails.setClientSecret(props.getClientSecret());
    resourceDetails.setAccessTokenUri(props.getTokenURL());
    if (scopes != null) {
      resourceDetails.setScope(scopes);
    }
    return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());
  }

}
