package org.hoverla.bibernate.datasource;

import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.configuration.DefaultConfiguration;
import org.hoverla.bibernate.connectionpool.pool.BibariPool;
import org.hoverla.bibernate.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = DefaultConfiguration.builder()
                .driver(TestConstants.DRIVER)
                .username(TestConstants.USERNAME)
                .password(TestConstants.PASSWORD)
                .url(TestConstants.JDBC_URL)
                .poolProvider(Configuration.ConnPoolProviderType.BIBARI)
                .poolSize(TestConstants.POOL_SIZE)
                .build();
    }

    @Test
    void createBibariDataSource() throws IllegalAccessException, NoSuchFieldException {
        BibariPool pool = new BibariPool(configuration);
        Field connectionPoolSizeField = pool.getClass().getDeclaredField("connectionPoolSize");
        connectionPoolSizeField.setAccessible(true);
        Integer connectionPoolSize = (Integer) connectionPoolSizeField.get(pool);
        assertEquals(TestConstants.POOL_SIZE, connectionPoolSize);
    }

    @Test
    void checkIfPoolIsActuallyPooled() throws SQLException {
        BibariPool pool = new BibariPool(configuration);

        Connection firstConnection = pool.getConnection();
        firstConnection.close();

        Connection secondConnection = pool.getConnection();
        secondConnection.close();

        assertSame(firstConnection, secondConnection);
    }
}
