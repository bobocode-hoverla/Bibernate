package org.hoverla.bibernate.connectionpool.exception;

/**
 * Throw this exception when we have troubles with configuration
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String msg) {
        super(msg);
    }
}
