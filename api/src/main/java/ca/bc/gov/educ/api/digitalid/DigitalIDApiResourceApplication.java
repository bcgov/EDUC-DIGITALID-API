package ca.bc.gov.educ.api.digitalid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("ca.bc.gov.educ.api.digitalid")
@ComponentScan("ca.bc.gov.educ.api.digitalid")
public class DigitalIDApiResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalIDApiResourceApplication.class, args);
	}

}

