package org.loader.exception;

/**
 * Exception thrown when an invalid label value is encountered.
 */
public class InvalidLabelException extends RuntimeException {

    private final String invalidLabel;

    public InvalidLabelException(String message, String invalidLabel) {
        super(message);
        this.invalidLabel = invalidLabel;
    }

    public String getInvalidLabel() {
        return invalidLabel;
    }
}