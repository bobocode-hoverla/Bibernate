package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.persistence.factory.SessionFactory;

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
