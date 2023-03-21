package org.hoverla.bibernate.exception.session.transaction;

public class TransactionalOperationException  extends RuntimeException {
    public TransactionalOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
