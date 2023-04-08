package org.hoverla.bibernate.session.transaction;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.session.Session;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used for creating a {@link Transaction} and attaching it to the current session
 */
@Slf4j
public class TransactionFactory {
    private final DataSource dataSource;

    /**
     Constructs a new TransactionFactory object with the given DataSource.
     @param dataSource the DataSource to use for creating Transaction objects
     */
    public TransactionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     Creates and returns a Transaction object for the given Session instance.
     @param session the Session instance to create a Transaction object for
     @return a Transaction object
     @throws SQLException if an error occurs while creating the Transaction object
     */
    public Transaction getTransactionForSession(Session session) throws SQLException {
        Connection connection = dataSource.getConnection();
        log.debug("Getting a new transaction for session: {}", session.toString());
        TransactionImpl transaction = new TransactionImpl(connection);
        transaction.setSession(session);
        return transaction;
    }
}
