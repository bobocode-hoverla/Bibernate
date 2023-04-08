package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.exception.configuration.ConfigurationException;
import org.hoverla.bibernate.session.factory.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration API used to configure datasource as to create SessionFactory
 */
public interface Configuration {
    /**
     Returns the URL of the data source.
     @return the URL of the data source as a String
     */
    String getUrl();

    /**
     Returns the username for accessing the data source.
     @return the username as a String
     */
    String getUsername();

    /**

     Returns the password for accessing the data source.
     @return the password as a String
     */
    String getPassword();
    /**

     Returns the driver used to connect to the data source.
     @return the driver as a String
     */
    String getDriver();
    /**

     Returns the size of the connection pool.
     @return the pool size as an Integer
     */
    Integer getPoolSize();
    /**

     Returns the type of connection pool provider.
     @return the pool provider as a ConnPoolProviderType
     */
    ConnPoolProviderType getPoolProvider();
    boolean isAutoDdl();
    /**

     Builds a session factory based on the configuration settings.
     @return the session factory as a SessionFactory object
     */
    SessionFactory buildSessionFactory();

    /**
     * Validates the correctness of configuration object.
     * Since configuration is used to initialize datasource, all properties should be non-null
     */
    default void validate() {
        Map<String, String> properties = new HashMap<>();
        properties.put("url", getUrl());
        properties.put("username", getUsername());
        properties.put("password", getPassword());
        properties.put("driver", getDriver());
        properties.entrySet().stream()
                .filter(entry -> Objects.isNull(entry.getValue()))
                .findFirst().ifPresent(Configuration::throwException);
    }

    /**
     * Enum of Connection Pool providers.
     * You can choose Hikari or our implementation - Bibari
     */
    enum ConnPoolProviderType {
        HIKARI("hikari"),
        BIBARI("bibari");

        private final String provider;

        ConnPoolProviderType(String provider) {
            this.provider = provider;
        }

        public String getProvider() {
            return provider;
        }

        public static ConnPoolProviderType fromValue(String value) {
            for (ConnPoolProviderType enumVal : ConnPoolProviderType.values()) {
                if (enumVal.provider.equalsIgnoreCase(value)) {
                    return enumVal;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }

    private static void throwException(Map.Entry<String, String> entry) {
        throw new ConfigurationException(String.format("'%s' property is null. Set up your configuration properly",
                entry.getKey()));
    }
}
