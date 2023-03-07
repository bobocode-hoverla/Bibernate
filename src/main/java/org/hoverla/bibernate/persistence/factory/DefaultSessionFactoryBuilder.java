package org.hoverla.bibernate.persistence.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;

import javax.sql.DataSource;

public class DefaultSessionFactoryBuilder implements SessionFactoryBuilder {

    @Override
    public SessionFactory build(Configuration conf) {
        Configuration.ConnPoolProviderType poolProvider = conf.getPoolProvider();
        DataSource dataSource = configureDatasourceBasedOnProvider(poolProvider, conf);
        return new SessionFactoryImpl(dataSource);
    }

    private DataSource configureDatasourceBasedOnProvider(Configuration.ConnPoolProviderType poolProvider, Configuration conf) {
        if (poolProvider == Configuration.ConnPoolProviderType.HIKARI) {
            return configureHikariDataSource(conf);
        } else if (poolProvider == Configuration.ConnPoolProviderType.BIBARI) {
            return configureBibariDataSource(conf);
        }
        return null;
    }

    private DataSource configureBibariDataSource(Configuration conf) {
        return new BibariDataSource(conf);
    }

    private DataSource configureHikariDataSource(final Configuration configuration) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(configuration.getDriver());
        hikariConfig.setJdbcUrl(configuration.getUrl());
        hikariConfig.setUsername(configuration.getUsername());
        hikariConfig.setPassword(configuration.getPassword());
        hikariConfig.setMaximumPoolSize(configuration.getPoolSize());
        return new HikariDataSource(hikariConfig);
    }
}
