package org.hoverla.bibernate.exception.session;

public class SessionOperationException extends RuntimeException {
    public SessionOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
