package org.hoverla.bibernate.session.transaction;

import org.hoverla.bibernate.session.Session;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionImplTest {

    private Connection connection;
    private TransactionImpl transaction;

    @BeforeEach
    public void setUp() {
        connection = mock(Connection.class);
        transaction = new TransactionImpl(connection);
    }

    @Test
    void testBegin() throws SQLException {
        transaction.begin();
        assertFalse(connection.getAutoCommit());
    }

    @Test
    void testCommit() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(false);
        transaction.begin();
        transaction.setSession(mock(Session.class));
        transaction.commit();
        assertTrue(transaction.isClosed());
        verify(connection, times(1)).commit();
        verify(connection).close();
        transaction.rollback();
        assertTrue(transaction.isClosed());
    }

    @Test
    void testRollback() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(false);
        transaction.begin();
        transaction.rollback();
        assertTrue(transaction.isClosed());
        verify(connection, times(1)).rollback();
        verify(connection).close();
        transaction.rollback();
        assertTrue(transaction.isClosed());
    }

    @Test
    void testIsClosed() {
        assertTrue(transaction.isClosed());
    }

}
