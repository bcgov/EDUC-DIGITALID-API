package ca.bc.gov.educ.api.digitalid.config;

import ca.bc.gov.educ.api.digitalid.helpers.LogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
@Slf4j
public class RequestResponseInterceptor implements AsyncHandlerInterceptor {

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
    final long startTime = Instant.now().toEpochMilli();
    request.setAttribute("startTime", startTime);
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
    response.setHeader("correlationID", request.getHeader("correlationID"));
  }


}
