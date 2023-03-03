package org.hoverla.bibernate.persistence.factory;

import javax.sql.DataSource;

public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;

    public SessionFactoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

   /*

    Will use datasource
    Our or hikari
    This datasource will be giving connections
    Session is wrapper of connection

    ...

     */

    @Override
    public Session openSession() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
