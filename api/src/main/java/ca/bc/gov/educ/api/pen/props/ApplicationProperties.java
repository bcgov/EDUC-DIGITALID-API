package ca.bc.gov.educ.api.pen.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class holds all application properties
 * 
 * @author Marco Villeneuve
 *
 */
@Component
public class ApplicationProperties {

	@Value("${oauth.server.url}")
	private String oauthServerURL;

	public String getOauthServerURL() {
		return oauthServerURL;
	}

}
