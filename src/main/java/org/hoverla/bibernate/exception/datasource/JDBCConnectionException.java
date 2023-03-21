package org.hoverla.bibernate.exception.datasource;

public class JDBCConnectionException extends RuntimeException {
    private static final String MESSAGE = "Unable to acquire JDBC Connection";

    public JDBCConnectionException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
