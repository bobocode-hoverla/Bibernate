package org.hoverla.bibernate.session.factory;

import org.hoverla.bibernate.session.DefaultSession;
import org.hoverla.bibernate.session.Session;

import javax.sql.DataSource;

public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;

    public SessionFactoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Session openSession() {
        return new DefaultSession(dataSource);
    }

    @Override
    public void close() {
    }
}
