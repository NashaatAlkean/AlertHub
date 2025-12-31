package org.loader.exception;

import org.loader.model.enums.Provider;

/**
 * Exception thrown when attempting to process a file that has already been processed.
 */
public class FileAlreadyProcessedException extends RuntimeException {

    private final Provider provider;
    private final String filename;

    public FileAlreadyProcessedException(String message, Provider provider, String filename) {
        super(message);
        this.provider = provider;
        this.filename = filename;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getFilename() {
        return filename;
    }
}