package de.daimler.heybeach.error;

public class BackendException extends Exception {
    public BackendException(String message) {
        super(message);
    }

    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
