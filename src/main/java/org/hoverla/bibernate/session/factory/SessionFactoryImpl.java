package org.hoverla.bibernate.session.factory;

import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.session.DefaultSession;
import org.hoverla.bibernate.session.Session;
import org.hoverla.bibernate.util.DdlGenerator;

import javax.sql.DataSource;

public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;
    private final DdlGenerator ddlGenerator;

    public SessionFactoryImpl(DataSource dataSource, Configuration conf) {
        this.dataSource = dataSource;
        this.ddlGenerator = new DdlGenerator(dataSource);
        generateDDl(conf);
    }

    private void generateDDl(Configuration conf) {
        if (conf.isAutoDdl()) {
            ddlGenerator.generateDdl();
        }
    }

    @Override
    public Session openSession() {
        return new DefaultSession(dataSource);
    }

    @Override
    public void close() {
    }
}
