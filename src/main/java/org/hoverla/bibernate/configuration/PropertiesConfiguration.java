package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.session.factory.DefaultSessionFactoryBuilder;
import org.hoverla.bibernate.session.factory.SessionFactory;
import org.hoverla.bibernate.util.PropertiesUtil;

import java.util.Properties;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static org.hoverla.bibernate.configuration.Configuration.ConnPoolProviderType.fromValue;

public class PropertiesConfiguration implements Configuration {

    private static final String DEFAULT_CONFIG_PROPERTIES_FILE_NAME = "application.properties";
    private Properties properties;

    public PropertiesConfiguration() {
        this(DEFAULT_CONFIG_PROPERTIES_FILE_NAME);
    }

    public PropertiesConfiguration(String propertiesFileName) {
        configure(propertiesFileName);
    }

    private void configure(String propertiesFileName) {
        Properties properties = PropertiesUtil.getPropertiesFrom(propertiesFileName);
        configure(properties);
    }

    public PropertiesConfiguration(Properties properties) {
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
        return parseInt(properties.getProperty(DbSettings.POOL_SIZE));
    }

    @Override
    public ConnPoolProviderType getPoolProvider() {
        return fromValue(properties.getProperty(DbSettings.CP_PROVIDER));
    }

    @Override
    public boolean isAutoDdlCreation() {
        return parseBoolean(properties.getProperty(DbSettings.AUTO_DLL));
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
        String AUTO_DLL = "db.auto.dll.creation";
    }
}
