package org.hoverla.bibernate.exception.pool;

public class CannotTakePoolConnectionException extends RuntimeException {
    public CannotTakePoolConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
