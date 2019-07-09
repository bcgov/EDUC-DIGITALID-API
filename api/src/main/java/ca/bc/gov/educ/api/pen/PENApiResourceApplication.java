package ca.bc.gov.educ.api.pen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ca.bc.gov.educ.api.pen")
public class PENApiResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PENApiResourceApplication.class, args);
	}

}

