package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.connectionpool.exception.ConfigurationException;
import org.hoverla.bibernate.persistence.factory.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration API used to configure datasource as to create SessionFactory
 */
public interface Configuration {
    String getUrl();

    String getUsername();

    String getPassword();

    String getDriver();

    Integer getPoolSize();

    ConnPoolProviderType getPoolProvider();

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
                .findFirst().ifPresent(entry -> {
                    throw new ConfigurationException(String.format("'%s' property is null. Set up your configuration properly",
                            entry.getKey()));
                });
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
}
