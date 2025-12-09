package com.jobportal.exception;

/**
 * Exception thrown when a request is invalid or violates business rules
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
