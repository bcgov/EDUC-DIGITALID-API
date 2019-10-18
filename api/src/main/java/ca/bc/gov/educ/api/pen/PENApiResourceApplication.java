package ca.bc.gov.educ.api.pen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("ca.bc.gov.educ.api.pen")
@ComponentScan("ca.bc.gov.educ.api.pen")
@EnableCaching
public class PENApiResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PENApiResourceApplication.class, args);
	}

}

