package org.hoverla.bibernate.configuration;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.session.factory.DefaultSessionFactoryBuilder;
import org.hoverla.bibernate.session.factory.SessionFactory;

@Builder
@Data
@Slf4j
public class DefaultConfiguration implements Configuration {

    private String url;
    private String username;
    private String password;
    private String driver;
    private String entityPackageToScan;

    @Builder.Default
    private Integer poolSize = 10;
    @Builder.Default
    private ConnPoolProviderType poolProvider = ConnPoolProviderType.HIKARI;

    @Override
    public SessionFactory buildSessionFactory() {
        log.info("Building session factory with provided configuration={}", this);
        return new DefaultSessionFactoryBuilder().build(this);
    }
}
