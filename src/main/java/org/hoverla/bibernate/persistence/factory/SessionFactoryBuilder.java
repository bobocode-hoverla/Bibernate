package org.hoverla.bibernate.persistence.factory;

import org.hoverla.bibernate.configuration.Configuration;

public interface SessionFactoryBuilder {
    SessionFactory build(Configuration conf);
}
