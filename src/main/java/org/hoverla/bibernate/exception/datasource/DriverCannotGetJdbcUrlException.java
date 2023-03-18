package org.hoverla.bibernate.exception.datasource;

public class DriverCannotGetJdbcUrlException extends RuntimeException {
    public DriverCannotGetJdbcUrlException(String message) {
        super(message);
    }
}
