package org.hoverla.bibernate.connectionpool.pool;

import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.util.DriverDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This is the primary connection pool class that provides the basic
 * pooling behavior for Bibari Connection Pool.
 */
public class BibariPool {

    private final Configuration config;
    private final BlockingQueue<Connection> connectionPool;
    private final int connectionPoolSize;
    private DataSource ds;

    /**
     * Construct a BibariPool with the specified configuration.
     *
     * @param config a Configuration instance
     */
    public BibariPool(Configuration config) {
        this.config = config;
        this.connectionPoolSize = config.getPoolSize();
        this.connectionPool = new ArrayBlockingQueue<>(connectionPoolSize);
        initializeDataSource();
        initializePool();
    }

    /**
     * Get a connection from the pool.
     *
     * @return a java.sql.Connection instance
     */
    public Connection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create/initialize the underlying DataSource.
     */
    private void initializeDataSource() {
        final var jdbcUrl = config.getUrl();
        final var username = config.getUsername();
        final var password = config.getPassword();
        final var driverClassName = config.getDriver();

        ds = new DriverDataSource(jdbcUrl, driverClassName, username, password);
    }


    /**
     * Initialized connection pool by connection wrapper class - WrapperConnection.
     */
    private void initializePool() {
        for (int i = 0; i < connectionPoolSize; i++) {
            connectionPool.add(new WrapperConnection(retrieveConnectionFromDataSource(), connectionPool));
        }
    }

    /**
     * Retrieves a raw Connection instance.
     */
    private Connection retrieveConnectionFromDataSource() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
