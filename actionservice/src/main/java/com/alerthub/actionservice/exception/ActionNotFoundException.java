package com.alerthub.actionservice.exception;

/**
 * Thrown when an Action is not found in the system.
 */
public class ActionNotFoundException extends RuntimeException {

    public ActionNotFoundException(String message) {
        super(message);
    }
}
