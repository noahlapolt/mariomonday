package mariomonday.backend.error.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mariomonday.backend.error.exceptions.AlreadyExistsException;

/**
 * Advice for AlreadyExistsException (409 status code)
 */
@RestControllerAdvice
class AlreadyExistsExceptionAdvice {

  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  String alreadyExistsHandler(AlreadyExistsException ex) {
    return ex.getMessage();
  }
}