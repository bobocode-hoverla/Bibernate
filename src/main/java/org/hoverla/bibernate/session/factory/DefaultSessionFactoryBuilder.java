package org.hoverla.bibernate.session.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;
import org.hoverla.bibernate.exception.ExceptionMessages;
import org.hoverla.bibernate.exception.datasource.DataSourceNotFoundException;

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
        String provider = conf.getPoolProvider().getProvider();
        throw new DataSourceNotFoundException(String.format(ExceptionMessages.DATA_SOURCE_NOT_FOUND_MSG, provider));
    }

    private DataSource configureBibariDataSource(final Configuration conf) {
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
