package ca.bc.gov.educ.api.digitalID.props;

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

	public static final String CLIENT_ID = "DIGITAL-ID-API";


    @Value("${oauth.server.url}")
	private String oauthServerURL;

	public String getOauthServerURL() {
		return oauthServerURL;
	}

	@Value("${oauth.server.checktoken.endpoint}")
	private String checkTokenURL;

	public String getCheckTokenEndpoint() {
		return checkTokenURL;
	}

}
