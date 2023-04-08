package org.hoverla.bibernate.session.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;
import org.hoverla.bibernate.exception.ExceptionMessages;
import org.hoverla.bibernate.exception.datasource.DataSourceNotFoundException;

import javax.sql.DataSource;

@Slf4j
public class DefaultSessionFactoryBuilder implements SessionFactoryBuilder {

    @Override
    public SessionFactory build(Configuration conf) {
        Configuration.ConnPoolProviderType poolProvider = conf.getPoolProvider();
        DataSource dataSource = configureDatasourceBasedOnProvider(poolProvider, conf);
        return new SessionFactoryImpl(dataSource, conf);
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

    /**
     Configures a BibariDataSource instance based on the given Configuration object.
     @param conf the Configuration object containing the necessary information to configure a BibariDataSource
     @return a BibariDataSource instance
     */
    private DataSource configureBibariDataSource(final Configuration conf) {
        log.debug("Configuring BibariDataSource with configuration: {}", conf.toString());
        return new BibariDataSource(conf);
    }

    /**

     Configures a HikariDataSource instance based on the given Configuration object.
     @param configuration the Configuration object containing the necessary information to configure a HikariDataSource
     @return a HikariDataSource instance
     */
    private DataSource configureHikariDataSource(final Configuration configuration) {
        log.debug("Configuring HikariDataSource with configuration: {}", configuration.toString());
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(configuration.getDriver());
        hikariConfig.setJdbcUrl(configuration.getUrl());
        hikariConfig.setUsername(configuration.getUsername());
        hikariConfig.setPassword(configuration.getPassword());
        hikariConfig.setMaximumPoolSize(configuration.getPoolSize());
        return new HikariDataSource(hikariConfig);
    }
}
