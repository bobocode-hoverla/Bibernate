package org.hoverla.bibernate.exception.queryexecutor;

/**
 * This class is an exception that is thrown when an error occurs while attempting
 * to execute a SQL query. It extends the RuntimeException class and has a constructor
 * that takes a message and a cause.
 */
public class UnableExecuteSqlQueryException extends RuntimeException {
    public UnableExecuteSqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
