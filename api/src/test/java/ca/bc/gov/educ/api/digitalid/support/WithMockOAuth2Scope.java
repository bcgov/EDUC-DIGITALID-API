package ca.bc.gov.educ.api.digitalid.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * How to test spring-security-oauth2 resource server security?
 * https://stackoverflow.com/a/40921028
 */

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2ScopeSecurityContextFactory.class)
public @interface WithMockOAuth2Scope {

    String scope() default "";
}