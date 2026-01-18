package com.alerthub.actionservice.exception;

/**
 * Thrown when an Action violates business rules.
 */
public class InvalidActionException extends RuntimeException {

    public InvalidActionException(String message) {
        super(message);
    }
}
