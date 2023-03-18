package org.hoverla.bibernate.exception.datasource;

/**
 * Exception is thrown when we could not find appropriate data source
 */
public class CannotGetPhysicalConnectionFromDatasourceException extends RuntimeException {
    public CannotGetPhysicalConnectionFromDatasourceException(String msg) {
        super(msg);
    }

    public CannotGetPhysicalConnectionFromDatasourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
