package org.hoverla.bibernate.persistence.factory;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hoverla.bibernate.configuration.Configuration;

public class DefaultSessionFactoryBuilder implements SessionFactoryBuilder {

    // private final DataSourceStrategy dataSource;
    // own or hikari

    @Override
    public SessionFactory build(Configuration conf) {
        Configuration.ConnPoolProviderType poolProvider = conf.getPoolProvider();
        SessionImpl session = null;
        SessionFactory sessionFactory = new SessionFactoryImpl(null);
        return sessionFactory;
    }
}
