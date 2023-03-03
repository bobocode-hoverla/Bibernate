package org.hoverla.bibernate.configuration;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hoverla.bibernate.persistence.factory.DefaultSessionFactoryBuilder;
import org.hoverla.bibernate.persistence.factory.SessionFactory;

@Builder
@ToString
@Data
public class DefaultConfiguration implements Configuration {

    private String url;
    private String username;
    private String password;
    private String driver;

    @Builder.Default
    private Integer poolSize = 10;
    @Builder.Default
    private ConnPoolProviderType poolProvider = ConnPoolProviderType.HIKARI;

    @Override
    public SessionFactory buildSessionFactory() {
        // TODO: Let user configure session factory builder (expand configuration) ??
        return new DefaultSessionFactoryBuilder().build(this);
    }
}
