package ca.bc.gov.educ.api.digitalid.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * How to test spring-security-oauth2 resource server security?
 * https://stackoverflow.com/a/40921028
 */

public class WithMockOAuth2ScopeSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Scope> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2Scope mockOAuth2Scope) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<String> scope = new HashSet<>();
        scope.add(mockOAuth2Scope.scope());

        OAuth2Request request = new OAuth2Request(null, null, null, true, scope, null, null, null, null);

        Authentication auth = new OAuth2Authentication(request, null);

        context.setAuthentication(auth);

        return context;
    }
}