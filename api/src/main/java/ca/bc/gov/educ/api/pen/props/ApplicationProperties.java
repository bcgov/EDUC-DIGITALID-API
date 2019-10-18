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

	public static String ORDS_URL;
	public static String ORDS_USERNAME;
	public static String ORDS_PASSWORD;

	@Value("${ords.url}")
	public void setOrdsURL(String ordsURL) {
		ORDS_URL = ordsURL;
	}

	@Value("${ords.username}")
	public void setOrdsUsername(String ordsUsername) {
		ORDS_USERNAME = ordsUsername;
	}

	@Value("${ords.password}")
	public void setOrdsPassword(String ordsPassword) {
		ORDS_PASSWORD = ordsPassword;
	}

    @Value("${oauth.server.url}")
	private String oauthServerURL;

	public String getOauthServerURL() {
		return oauthServerURL;
	}

}
