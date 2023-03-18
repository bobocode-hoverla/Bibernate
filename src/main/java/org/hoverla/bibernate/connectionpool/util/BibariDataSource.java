package org.hoverla.bibernate.connectionpool.util;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.Constants;
import org.hoverla.bibernate.connectionpool.pool.BibariPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 * The Bibari pooled DataSource.
 */
@Slf4j
public class BibariDataSource implements DataSource {

    private final BibariPool pool;
    private static final Logger parentLogger = Logger.getLogger(BibariDataSource.class.getSimpleName());

    /**
     * Construct a BibariDataSource with the specified configuration. The
     * {@link Configuration} is copied and the pool is started by invoking this
     * constructor.
     *
     * @param configuration a Configuration instance
     */
    public BibariDataSource(Configuration configuration) {
        configuration.validate();

        log.info("{} - Starting...", Constants.POOL_NAME);
        pool = new BibariPool(configuration);
        log.info("{} - Start completed.", Constants.POOL_NAME);
    }

    /**
     * @return a WrapperConnection instance from Bibari Connection Pool
     */
    @Override
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
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
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return parentLogger;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
