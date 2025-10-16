package mariomonday.backend.error.exceptions;

/**
 * Exception when a request is invalid
 */
public class InvalidRequestException extends RuntimeException {

  public InvalidRequestException(String message) {
    super(message);
  }
}
