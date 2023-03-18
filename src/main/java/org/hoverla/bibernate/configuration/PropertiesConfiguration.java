package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.persistence.factory.DefaultSessionFactoryBuilder;
import org.hoverla.bibernate.persistence.factory.SessionFactory;
import org.hoverla.bibernate.util.PropertiesUtil;

import java.util.Properties;

public class PropertiesConfiguration implements Configuration {

    private static final String DEFAULT_CONFIG_PROPERTIES_FILE_NAME = "application.properties";
    private Properties properties;

    public PropertiesConfiguration() {
        this(DEFAULT_CONFIG_PROPERTIES_FILE_NAME);
    }

    public PropertiesConfiguration(Properties properties) {
        configure(properties);
    }

    public PropertiesConfiguration(String propertiesFileName) {
        configure(propertiesFileName);
    }

    private void configure(String propertiesFileName) {
        Properties properties = PropertiesUtil.getPropertiesFrom(propertiesFileName);
        configure(properties);
    }

    private void configure(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getUrl() {
        return properties.getProperty(DbSettings.URL);
    }

    @Override
    public String getUsername() {
        return properties.getProperty(DbSettings.USERNAME);
    }

    @Override
    public String getPassword() {
        return properties.getProperty(DbSettings.PASSWORD);
    }

    @Override
    public String getDriver() {
        return properties.getProperty(DbSettings.DRIVER);
    }

    @Override
    public Integer getPoolSize() {
        return Integer.parseInt(properties.getProperty(DbSettings.POOL_SIZE));
    }

    @Override
    public ConnPoolProviderType getPoolProvider() {
        return ConnPoolProviderType.fromValue(properties.getProperty(DbSettings.CP_PROVIDER));
    }

    @Override
    public SessionFactory buildSessionFactory() {
        return new DefaultSessionFactoryBuilder().build(this);
    }

    private interface DbSettings {
        String URL = "db.url";
        String USERNAME = "db.username";
        String PASSWORD = "db.password";
        String DRIVER = "db.driver";
        String POOL_SIZE = "db.pool.size";
        String CP_PROVIDER = "db.pool.provider";
    }
}
