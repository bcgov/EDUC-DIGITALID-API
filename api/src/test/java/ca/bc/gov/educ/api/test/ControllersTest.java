package ca.bc.gov.educ.api.test;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import ca.bc.gov.educ.api.pen.controller.PublicController;
import ca.bc.gov.educ.api.pen.controller.UserController;
 
public class ControllersTest {
	
	@Test
	public void testGetPublicController() {
		PublicController controller = new PublicController();
		assertNotNull(controller.getGreeting());
	}
	
	@Test
	public void testGetUserController() {
		OAuth2Request req = new OAuth2Request(null, "test", null, false, null, null, "http://www.sample.com", null, null);
		@SuppressWarnings("serial")
		Authentication authen = new Authentication() {
			
			@Override
			public String getName() {
				return null;
			}
			
			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			}
			
			@Override
			public boolean isAuthenticated() {
				return false;
			}
			
			@Override
			public Object getPrincipal() {
				return null;
			}
			
			@Override
			public Object getDetails() {
				return null;
			}
			
			@Override
			public Object getCredentials() {
				return null;
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		};
		OAuth2Authentication auth = new OAuth2Authentication(req, authen);
		UserController controller = new UserController();
		assertNotNull(controller.getOauth2Principal(auth));
	}
}