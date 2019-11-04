package ca.bc.gov.educ.api.test;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.bc.gov.educ.api.digitalID.controller.PublicController;
import ca.bc.gov.educ.api.digitalID.props.ApplicationProperties;
 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationProperties.class)
@TestPropertySource("classpath:application.test.properties")
public class PropertiesTest {
	
    @Autowired
    private ApplicationProperties props;
 
	@Test
	public void testGetApplicationProperties() {
		assertNotNull(props);
		assertNotNull(props.getOauthServerURL());
	}
 
	@Test
	public void testGetPublicController() {
		PublicController controller = new PublicController();
		assertNotNull(controller.getGreeting());
	}
}