package org.hoverla.bibernate.exception.datasource;

public class CannotRetrieveConnectionException extends RuntimeException {
    public CannotRetrieveConnectionException(String message) {
        super(message);
    }

    public CannotRetrieveConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
