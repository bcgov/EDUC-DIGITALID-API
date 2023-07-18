package ca.bc.gov.educ.api.digitalid.config;

import ca.bc.gov.educ.api.digitalid.helpers.LogHelper;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
@Slf4j
public class RequestResponseInterceptor implements AsyncHandlerInterceptor {

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
    // for async this is called twice so need a check to avoid setting twice.
    if(request.getAttribute("startTime") == null){
      final long startTime = Instant.now().toEpochMilli();
      request.setAttribute("startTime", startTime);
    }
    return true;
  }

  /**
   * After completion.
   *
   * @param request  the request
   * @param response the response
   * @param handler  the handler
   * @param ex       the ex
   */
  @Override
  public void afterCompletion(@NonNull final HttpServletRequest request, final HttpServletResponse response, @NonNull final Object handler, final Exception ex) {
    LogHelper.logServerHttpReqResponseDetails(request, response);
    val correlationID = request.getHeader(ApplicationProperties.CORRELATION_ID);
    if (correlationID != null) {
      response.setHeader(ApplicationProperties.CORRELATION_ID, request.getHeader(ApplicationProperties.CORRELATION_ID));
    }
  }


}
