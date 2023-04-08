package org.hoverla.bibernate.connectionpool.pool;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.util.DriverDataSource;
import org.hoverla.bibernate.exception.ExceptionMessages;
import org.hoverla.bibernate.exception.datasource.CannotGetPhysicalConnectionFromDatasourceException;
import org.hoverla.bibernate.exception.pool.CannotTakePoolConnectionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This is the primary connection pool class that provides the basic
 * pooling behavior for Bibari Connection Pool.
 */
@Slf4j
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
        log.info("Creating Bibary Connection Pool..");
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
            throw new CannotTakePoolConnectionException(ExceptionMessages.CANNOT_TAKE_CONNECTION_FROM_POOL, e);
        }
    }

    /**
     * Create/initialize the underlying DataSource.
     */
    private void initializeDataSource() {
        log.debug("Initializing underlying driver datasource");
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
        log.debug("Initializing pool by getting physical connection from non-pooled datasource");
        for (int i = 0; i < connectionPoolSize; i++) {
            WrapperConnection connection = new WrapperConnection(retrieveConnectionFromDataSource(), connectionPool);
            connectionPool.add(connection);
        }
    }

    /**
     * Retrieves a raw Connection instance.
     */
    private Connection retrieveConnectionFromDataSource() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new CannotGetPhysicalConnectionFromDatasourceException(ExceptionMessages.CANNOT_GET_PHYSICAL_CONNECTION, e);
        }
    }
}
