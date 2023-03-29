package org.hoverla.bibernate.session.transaction;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.exception.session.transaction.TransactionalOperationException;
import org.hoverla.bibernate.session.Session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class TransactionImpl implements Transaction {
    private final Connection connection;
    private boolean isClosed = true;
    @Setter
    private Session session;

    public TransactionImpl(Connection connection) {
        Objects.requireNonNull(connection, "Parameter [connection] must not be null!");
        this.connection = connection;
    }

    @Override
    public void begin() {
        try {
            connection.setAutoCommit(false);
            isClosed = false;
            log.trace("Transaction is started.");
        } catch (SQLException e) {
            throw new TransactionalOperationException("Could not start transaction", e);
        }
    }

    @Override
    public void commit() {
        try (connection) {
            if (!connection.getAutoCommit()) {
                log.trace("Committing transaction.");
                this.session.flush();
                this.connection.commit();
            }
            this.isClosed = true;
        } catch (SQLException e) {
            throw new TransactionalOperationException("Could not commit transaction", e);
        }
    }

    @Override
    public void rollback() {
        try (connection) {
            if (!connection.getAutoCommit()) {
                log.trace("Rolling back transaction.");
                this.connection.rollback();
                log.trace("Transaction is rolled back.");
            }
            this.isClosed = true;
        } catch (SQLException e) {
            throw new TransactionalOperationException("Could not rollback transaction", e);
        }
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
}
