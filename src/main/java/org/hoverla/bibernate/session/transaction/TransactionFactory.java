package org.hoverla.bibernate.session.transaction;

import lombok.RequiredArgsConstructor;
import org.hoverla.bibernate.session.Session;

import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * This class is used for creating a {@link Transaction} and attaching it to the current session
 */
@RequiredArgsConstructor
public class TransactionFactory {
    private final DataSource dataSource;

    public Transaction getTransactionForSession(Session session) throws SQLException {
        TransactionImpl transaction = new TransactionImpl(dataSource.getConnection());
        transaction.setSession(session);
        return transaction;
    }
}
