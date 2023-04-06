package org.hoverla.bibernate.session.transaction.manager;

import org.hoverla.bibernate.connectionpool.pool.WrapperConnection;
import org.hoverla.bibernate.exception.session.transaction.TransactionalOperationException;
import org.hoverla.bibernate.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TransactionManagerImplTest {

    private TransactionManagerImpl transactionManager;

    @Mock
    private DataSource dataSource;

    @Mock
    private Session session;

    @Mock
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        ThreadLocal<Connection> connections = new ThreadLocal<>();
        connections.set(new WrapperConnection(null, null));


        when(dataSource.getConnection()).thenReturn(connection);

        transactionManager = new TransactionManagerImpl(dataSource, session);
    }

    @Test
    void testBegin() throws SQLException {
        transactionManager.begin();
        Assertions.assertNotNull(transactionManager.get());
    }

    @Test
    void testBeginWithExistingTransaction() {
        transactionManager.begin();
        Assertions.assertThrows(IllegalStateException.class, () -> transactionManager.begin());
    }

    @Test
    void testCommit() throws SQLException {
        transactionManager.begin();
        Assertions.assertNotNull(transactionManager.get());

        transactionManager.commit();
        transactionManager.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> transactionManager.get());
    }

    @Test
    void testCommitWithoutTransaction() {
        Assertions.assertThrows(IllegalStateException.class, () -> transactionManager.commit());
    }

    @Test
    void testCommitFails() throws SQLException {
        transactionManager.begin();
        Assertions.assertNotNull(transactionManager.get());

        doThrow(new SQLException("test")).when(connection).commit();
        Assertions.assertThrows(TransactionalOperationException.class, () -> transactionManager.commit());
    }

    @Test
    void testRollback() throws SQLException {
        transactionManager.begin();
        Assertions.assertNotNull(transactionManager.get());

        transactionManager.rollback();
        transactionManager.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> transactionManager.get());
    }

    @Test
    void testRollbackWithoutTransaction() {
        Assertions.assertThrows(IllegalStateException.class, () -> transactionManager.rollback());
    }

    @Test
    void testRollbackFails() throws SQLException {
        transactionManager.begin();
        Assertions.assertNotNull(transactionManager.get());

        doThrow(new SQLException("test")).when(connection).rollback();
        Assertions.assertThrows(TransactionalOperationException.class, () -> transactionManager.rollback());
    }
}
