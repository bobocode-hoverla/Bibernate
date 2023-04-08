package org.hoverla.bibernate.connectionpool.util;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.exception.datasource.DriverCannotGetJdbcUrlException;
import org.hoverla.bibernate.exception.datasource.UnableGetDriverInstanceException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

/**
 * Utility data source to get low-level connections to filling the pool.
 */
@Slf4j
public class DriverDataSource implements DataSource {

    private static final String PASSWORD = "password";
    private static final String USER = "user";

    private final String jdbcUrl;
    private final Properties driverProperties;
    private Driver driver;

    /**
     * Constructs a new {@code DriverDataSource} instance with the specified JDBC URL, driver class name, username,
     * and password.
     *
     * @param jdbcUrl the JDBC URL of the database
     * @param driverClassName the fully qualified class name of the JDBC driver to use
     * @param username the username to use when connecting to the database
     * @param password the password to use when connecting to the database
     */
    public DriverDataSource(String jdbcUrl, String driverClassName, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.driverProperties = new Properties();
        initUnderlyingDriver(jdbcUrl, driverClassName, username, password);
    }

    /**
     * Gets a connection from the underlying JDBC driver.
     *
     * @return a new {@link Connection} instance
     * @throws SQLException if an error occurs while getting the connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        return driver.connect(jdbcUrl, driverProperties);
    }

    /**
     * Gets a connection from the underlying JDBC driver with the specified username and password.
     *
     * @param username the username to use when connecting to the database
     * @param password the password to use when connecting to the database
     * @return a new {@link Connection} instance
     * @throws SQLException if an error occurs while getting the connection
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        final var cloned = (Properties) driverProperties.clone();
        if (username != null) {
            cloned.put(USER, username);
            if (cloned.containsKey("username")) {
                cloned.put("username", username);
            }
        }
        if (password != null) {
            cloned.put(PASSWORD, password);
        }

        return driver.connect(jdbcUrl, cloned);
    }

    /**

     Initializes the underlying JDBC driver by adding any provided credentials to the driver properties, then attempting
     to load the driver specified by the given class name. If the driver is not found, it will be searched for using the
     given JDBC URL instead. If no driver is found, a {@link UnableGetDriverInstanceException} will be thrown.
     @param jdbcUrl the JDBC URL to use to search for the driver if it is not found by class name
     @param driverClassName the fully qualified class name of the JDBC driver to use
     @param username the username to use when connecting to the database
     @param password the password to use when connecting to the database
     @throws UnableGetDriverInstanceException if a driver cannot be found or instantiated
     */
    private void initUnderlyingDriver(String jdbcUrl, String driverClassName, String username, String password) {
        putCredentialInProperties(username, password);
        findDriverByClassName(driverClassName);
        findDriverByUrlIfNotFound(jdbcUrl, driverClassName);
    }

    private void findDriverByUrlIfNotFound(String jdbcUrl, String driverClassName) {
        final var sanitizedUrl = jdbcUrl.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
        try {
            if (driver == null) {
                driver = DriverManager.getDriver(jdbcUrl);
                log.debug("Loaded driver with class name {} for jdbcUrl={}", driver.getClass().getName(), sanitizedUrl);
            } else if (!driver.acceptsURL(jdbcUrl)) {
                throw new DriverCannotGetJdbcUrlException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
            }
        } catch (SQLException e) {
            throw new UnableGetDriverInstanceException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
        }
    }

    private void putCredentialInProperties(String username, String password) {
        if (username != null) {
            driverProperties.put(USER, driverProperties.getProperty(USER, username));
        }
        if (password != null) {
            driverProperties.put(PASSWORD, driverProperties.getProperty(PASSWORD, password));
        }
    }

    private void findDriverByClassName(String driverClassName) {
        if (driverClassName != null) {
            var drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                var foundDriver = drivers.nextElement();
                if (foundDriver.getClass().getName().equals(driverClassName)) {
                    driver = foundDriver;
                    break;
                }
            }

            if (driver == null) {
                log.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", driverClassName);
                Class<?> driverClass = null;
                var threadContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    if (threadContextClassLoader != null) {
                        try {
                            driverClass = threadContextClassLoader.loadClass(driverClassName);
                            log.debug("Driver class {} found in Thread context class loader {}", driverClassName, threadContextClassLoader);
                        } catch (ClassNotFoundException e) {
                            log.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}",
                                    driverClassName, threadContextClassLoader, this.getClass().getClassLoader());
                        }
                    }

                    if (driverClass == null) {
                        driverClass = this.getClass().getClassLoader().loadClass(driverClassName);
                        log.debug("Driver class {} found in the Configuration class classloader {}", driverClassName, this.getClass().getClassLoader());
                    }
                } catch (ClassNotFoundException e) {
                    log.debug("Failed to load driver class {} from Configuration class classloader {}", driverClassName, this.getClass().getClassLoader());
                }

                if (driverClass != null) {
                    try {
                        driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        log.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", driverClassName, e);
                    }
                }
            }
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
