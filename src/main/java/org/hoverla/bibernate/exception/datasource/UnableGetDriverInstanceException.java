package org.hoverla.bibernate.exception.datasource;

public class UnableGetDriverInstanceException extends RuntimeException {
    public UnableGetDriverInstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
