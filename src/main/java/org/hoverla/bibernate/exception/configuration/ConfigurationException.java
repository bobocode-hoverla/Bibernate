package org.hoverla.bibernate.exception.configuration;

/**
 * Exception is thrown when we have troubles with configuration
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String msg) {
        super(msg);
    }
}
