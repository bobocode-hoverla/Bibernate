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
     * Gets the JDBC URL for the data source.
     *
     * @return the JDBC URL
     */
    String getUrl();

    /**
     * Gets the username for the data source.
     *
     * @return the username
     */
    String getUsername();

    /**
     * Gets the password for the data source.
     *
     * @return the password
     */
    String getPassword();

    /**
     * Gets the JDBC driver class for the data source.
     *
     * @return the JDBC driver class
     */
    String getDriver();

    /**
     * Gets the maximum number of connections to be maintained in the connection pool.
     *
     * @return the maximum number of connections
     */
    Integer getPoolSize();

    /**
     * Gets the type of connection pool provider to use.
     *
     * @return the connection pool provider type
     */
    ConnPoolProviderType getPoolProvider();

    /**
     * Gets the package name containing the entity classes.
     *
     * @return the entity package name
     */
    String getEntitiesPackage();

    /**
     * Builds a new session factory using the provided configuration settings.
     *
     * @return a new session factory
     */
    SessionFactory buildSessionFactory();

    /**
     * Returns a boolean indicating whether automatic DDL creation is enabled.
     *
     * @return true if automatic DDL creation is enabled, false otherwise
     */
    boolean isAutoDdlCreation();

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
