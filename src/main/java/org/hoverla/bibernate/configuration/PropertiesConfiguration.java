package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.session.factory.DefaultSessionFactoryBuilder;
import org.hoverla.bibernate.session.factory.SessionFactory;
import org.hoverla.bibernate.util.PropertiesUtil;

import java.util.Properties;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static org.hoverla.bibernate.configuration.Configuration.ConnPoolProviderType.fromValue;

/**
 * A {@link Configuration} implementation that reads configuration settings from a properties file.
 */
public class PropertiesConfiguration implements Configuration {

    /**
     * The default file name of the configuration properties file.
     */
    private static final String DEFAULT_CONFIG_PROPERTIES_FILE_NAME = "application.properties";

    /**
     * The properties object containing the configuration settings.
     */
    private Properties properties;

    /**
     * Constructs a new {@code PropertiesConfiguration} instance with the default properties file name.
     * The default properties file name is {@code application.properties}.
     */
    public PropertiesConfiguration() {
        this(DEFAULT_CONFIG_PROPERTIES_FILE_NAME);
    }

    /**
     * Constructs a new {@code PropertiesConfiguration} instance with the specified properties file name.
     *
     * @param propertiesFileName the name of the properties file to load configuration from
     */
    public PropertiesConfiguration(String propertiesFileName) {
        configure(propertiesFileName);
    }

    /**
     * Constructs a new {@code PropertiesConfiguration} instance with the specified {@link Properties} object.
     *
     * @param properties the {@link Properties} object containing the configuration settings
     */
    public PropertiesConfiguration(Properties properties) {
        configure(properties);
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
        return properties.getProperty(ConfigSettings.URL);
    }

    @Override
    public String getUsername() {
        return properties.getProperty(ConfigSettings.USERNAME);
    }

    @Override
    public String getPassword() {
        return properties.getProperty(ConfigSettings.PASSWORD);
    }

    @Override
    public String getDriver() {
        return properties.getProperty(ConfigSettings.DRIVER);
    }

    @Override
    public Integer getPoolSize() {
        return parseInt(properties.getProperty(ConfigSettings.POOL_SIZE));
    }

    @Override
    public ConnPoolProviderType getPoolProvider() {
        return fromValue(properties.getProperty(ConfigSettings.CP_PROVIDER));
    }

    @Override
    public String getEntitiesPackage() {
        return properties.getProperty(ConfigSettings.ENTITY_PACKAGE);
    }

    @Override
    public boolean isAutoDdlCreation() {
        return parseBoolean(properties.getProperty(ConfigSettings.AUTO_DLL));
    }

    @Override
    public SessionFactory buildSessionFactory() {
        return new DefaultSessionFactoryBuilder().build(this);
    }

    /**
     * Defines the configuration settings keys for the properties file.
     */
    private interface ConfigSettings {
        String URL = "db.url";
        String USERNAME = "db.username";
        String PASSWORD = "db.password";
        String DRIVER = "db.driver";
        String POOL_SIZE = "db.pool.size";
        String CP_PROVIDER = "db.pool.provider";
        String AUTO_DLL = "db.auto.dll.creation";
        String ENTITY_PACKAGE = "entity.package";
    }
}
