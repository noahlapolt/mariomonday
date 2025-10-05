package mariomonday.backend.error.exceptions;

/**
 * Exception when a GET/PATCH/DELETE request is for a non-existent entity
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}