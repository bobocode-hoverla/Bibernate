package org.hoverla.bibernate.session.transaction.manager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {
    Connection get() throws SQLException;

    DataSource getRawDataSource();
    void begin();

    void commit();

    void rollback();

    void clear();
}
