package ca.bc.gov.educ.api.digitalid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class DigitalIDApiResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalIDApiResourceApplication.class, args);
    }

    @Configuration
    static
    class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/v3/api-docs/**",
                    "/actuator/**",
                    "/swagger-ui/**");
        }

    }
}

