package org.hoverla.bibernate.connectionpool.util;

import lombok.extern.slf4j.Slf4j;

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

    public DriverDataSource(String jdbcUrl, String driverClassName, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.driverProperties = new Properties();

        if (username != null) {
            driverProperties.put(USER, driverProperties.getProperty(USER, username));
        }
        if (password != null) {
            driverProperties.put(PASSWORD, driverProperties.getProperty(PASSWORD, password));
        }

        if (driverClassName != null) {
            var drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                var d = drivers.nextElement();
                if (d.getClass().getName().equals(driverClassName)) {
                    driver = d;
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

        final var sanitizedUrl = jdbcUrl.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
        try {
            if (driver == null) {
                driver = DriverManager.getDriver(jdbcUrl);
                log.debug("Loaded driver with class name {} for jdbcUrl={}", driver.getClass().getName(), sanitizedUrl);
            } else if (!driver.acceptsURL(jdbcUrl)) {
                throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return driver.connect(jdbcUrl, driverProperties);
    }

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
