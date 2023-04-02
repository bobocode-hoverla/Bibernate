package org.hoverla.bibernate.session.transaction.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.connectionpool.pool.WrapperConnection;
import org.hoverla.bibernate.exception.session.transaction.TransactionalOperationException;
import org.hoverla.bibernate.session.Session;

@Slf4j
@RequiredArgsConstructor
public class TransactionManagerImpl implements TransactionManager {

    private static final Connection TRANSACTION_MARKER = new WrapperConnection(null, null);
    private final ThreadLocal<Connection> connections = new ThreadLocal<>();
    @Getter
    private final DataSource rawDataSource;
    private final Session session;
    @Override
    public Connection get() throws SQLException {
        Connection delegatingConnection = connections.get();
        checkConnectionActive(delegatingConnection);

        if (delegatingConnection == TRANSACTION_MARKER) {
            try (Connection physicalConnection = rawDataSource.getConnection()) {
                physicalConnection.setAutoCommit(false);
                log.debug("Obtained a new DB connection");
                connections.set(physicalConnection);
                return physicalConnection;
            }
        }

        return delegatingConnection;
    }

    @Override
    public void begin() {
        verifyNoOngoingTransaction();

        // setting a special marker to indicate that transaction has been started
        connections.set(TRANSACTION_MARKER);
        log.debug("Transaction has started");
    }

    @Override
    public void commit() {
        Optional
            .ofNullable(connections.get())
            .ifPresentOrElse(
                rawConnection -> {
                    try {
                        session.flush();
                        rawConnection.commit();
                        rawConnection.close();
                        log.debug("Transaction has been successfully committed");
                    } catch (Exception ex) {
                        throw new TransactionalOperationException("Exception during transaction commit", ex);
                    }
                },
                () -> checkConnectionActive(connections.get())
            );
    }

    @Override
    public void rollback() {
        Optional
            .ofNullable(connections.get())
            .ifPresentOrElse(
                rawConnection -> {
                    try {
                        rawConnection.rollback();
                        rawConnection.close();
                        log.debug("Transaction has been successfully rolled back");
                    } catch (Exception ex) {
                        throw new TransactionalOperationException("Exception during transaction rollback", ex);
                    }
                },
                () -> checkConnectionActive(connections.get())
            );
    }

    @Override
    public void clear() {
        connections.remove();
    }

    private void checkConnectionActive(Connection connection) {
        if (connection == null) {
            throw new IllegalStateException("Transaction is not active.");
        }
    }

    private void verifyNoOngoingTransaction() {
        if (connections.get() != null) {
            throw new IllegalStateException("Transaction is already in progress.");
        }
    }
}
