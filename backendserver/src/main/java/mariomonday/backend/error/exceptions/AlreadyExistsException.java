package mariomonday.backend.error.exceptions;

/**
 * Exception when there is a conflict for a unique field when creating/updating
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}