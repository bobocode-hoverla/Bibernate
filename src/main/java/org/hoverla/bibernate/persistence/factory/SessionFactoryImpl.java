package org.hoverla.bibernate.persistence.factory;

import org.hoverla.bibernate.persistence.session.Session;
import org.hoverla.bibernate.persistence.session.SessionImpl;

import javax.sql.DataSource;

public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;

    public SessionFactoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Session openSession() {
        return new SessionImpl(dataSource);
    }

    @Override
    public void close() throws Exception {

    }
}
