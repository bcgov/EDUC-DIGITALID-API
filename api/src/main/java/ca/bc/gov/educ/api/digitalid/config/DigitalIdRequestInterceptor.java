package ca.bc.gov.educ.api.digitalid.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class DigitalIdRequestInterceptor extends HandlerInterceptorAdapter {

  private static final Logger log = LoggerFactory.getLogger(DigitalIdRequestInterceptor.class);
  public static final String RESPONSE_STATUS = "RESPONSE STATUS: {}";

  @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod() != null && request.getRequestURL() != null)
            log.info("{} {}", request.getMethod(), request.getRequestURL());
        if (request.getQueryString() != null)
            log.debug("Query string     : {}", request.getQueryString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int status = response.getStatus();
        if(status == 404 || (status >= 200 && status < 300)) {
            log.info(RESPONSE_STATUS, status);
        } else {
            log.error(RESPONSE_STATUS, status);
        }
    }
}
