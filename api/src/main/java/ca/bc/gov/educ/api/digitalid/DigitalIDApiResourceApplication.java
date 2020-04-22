package ca.bc.gov.educ.api.digitalid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
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
              "/actuator/health","/actuator/prometheus",
              "/swagger-ui/**", "/health");
    }
  }
}

