package org.hoverla.bibernate.exception.datasource;

/**
 * Exception is thrown when we could not find appropriate data source
 */
public class DataSourceNotFoundException extends RuntimeException {
    public DataSourceNotFoundException(String msg) {
        super(msg);
    }
}