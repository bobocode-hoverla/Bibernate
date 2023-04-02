package org.hoverla.bibernate.session.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.hoverla.bibernate.session.transaction.manager.TransactionManager;

@RequiredArgsConstructor
public class DelegatingDataSource implements DataSource {

    @Delegate(excludes = Exclude.class)
    private final DataSource delegate;

    private final TransactionManager transactionManager;

    @Override
    public Connection getConnection() throws SQLException {
        return transactionManager.get();
    }

    private interface Exclude {
        void getConnection();
    }
}