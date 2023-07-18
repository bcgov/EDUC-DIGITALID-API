package ca.bc.gov.educ.api.digitalid.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

@ControllerAdvice
public class CustomRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

  HttpServletRequest httpServletRequest;

  @Autowired
  public void setHttpServletRequest(final HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  @Override
  public boolean supports(final MethodParameter methodParameter, final Type type, final Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  @Override
  public Object afterBodyRead(final Object body, final HttpInputMessage inputMessage, final MethodParameter parameter, final Type targetType,
                              final Class<? extends HttpMessageConverter<?>> converterType) {
    this.httpServletRequest.setAttribute("payload", body);
    return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
  }
}
