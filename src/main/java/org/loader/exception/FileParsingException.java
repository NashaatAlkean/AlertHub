package org.loader.exception;

/**
 * Exception thrown when file parsing fails.
 */
public class FileParsingException extends RuntimeException {

    private final String filename;

    public FileParsingException(String message, String filename) {
        super(message);
        this.filename = filename;
    }

    public FileParsingException(String message, String filename, Throwable cause) {
        super(message, cause);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}