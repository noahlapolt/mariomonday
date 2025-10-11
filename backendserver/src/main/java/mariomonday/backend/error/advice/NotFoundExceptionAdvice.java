package mariomonday.backend.error.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mariomonday.backend.error.exceptions.NotFoundException;

/**
 * Advice for NotFoundException (404 status code)
 */
@RestControllerAdvice
class NotFoundExceptionAdvice {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String notFoundHandler(NotFoundException ex) {
    return ex.getMessage();
  }
}