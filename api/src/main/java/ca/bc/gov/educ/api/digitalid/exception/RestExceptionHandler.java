package ca.bc.gov.educ.api.digitalid.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ca.bc.gov.educ.api.digitalid.exception.errors.ApiError;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


  /**
   * Handles HttpMessageNotReadableException
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    String error = "Malformed JSON request";
    return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  /**
   * Handles InvalidParameterException
   *
   * @param ex the InvalidParameterException
   * @return the ApiError object
   */
  @ExceptionHandler(InvalidParameterException.class)
  protected ResponseEntity<Object> handleInvalidParameter(InvalidParameterException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
   *
   * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers,
          HttpStatus status,
          WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
   *
   * @param ex the EntityNotFoundException
   * @return the ApiError object
   */
  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(
          EntityNotFoundException ex) {
    ApiError apiError = new ApiError(NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(InvalidPayloadException.class)
  protected ResponseEntity<Object> handleInvalidPayloadException(
          InvalidPayloadException ex) {
    return buildResponseEntity(ex.getError());
  }

}