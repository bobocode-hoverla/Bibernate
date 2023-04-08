package org.hoverla.bibernate.session.factory;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.session.DefaultSession;
import org.hoverla.bibernate.session.Session;
import org.hoverla.bibernate.util.DdlGenerator;

import javax.sql.DataSource;

@Slf4j
public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;
    private final DdlGenerator ddlGenerator;

    public SessionFactoryImpl(DataSource dataSource, Configuration configuration) {
        log.debug("Starting session factory");
        this.dataSource = dataSource;
        this.ddlGenerator = new DdlGenerator(dataSource);
        checkAutoDdlCreation(configuration);
    }

    @Override
    public Session openSession() {
        log.debug("Opening new session");
        return new DefaultSession(dataSource);
    }

    @Override
    public void close() throws Exception {
        log.info("Closing SessionFactory and all resources");
    }

    private void checkAutoDdlCreation(Configuration configuration) {
        var isAutoDdl = configuration.isAutoDdlCreation();
        if (isAutoDdl) {
            log.debug("Auto DDL generation started for package: {}", configuration.getEntitiesPackage());
            ddlGenerator.generateDdl(configuration.getEntitiesPackage());
            log.info("Auto DDL generation completed for package: {}", configuration.getEntitiesPackage());
        }
    }
}