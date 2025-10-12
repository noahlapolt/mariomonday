package mariomonday.backend.error.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mariomonday.backend.error.exceptions.InvalidRequestException;

/**
 * Advice for InvalidRequestException (400 status code)
 */
@RestControllerAdvice
class InvalidRequestExceptionAdvice {

  @ExceptionHandler(InvalidRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String invalidRequestHandler(InvalidRequestException ex) {
    return ex.getMessage();
  }
}