package org.hoverla.bibernate.persistence.factory;

import org.hoverla.bibernate.configuration.Configuration;

/**
 * The contract for building a {@link org.hoverla.bibernate.persistence.factory.Session} given a configuration.
 */
public interface SessionFactoryBuilder {
    /**
     * Builds the SessionFactory.
     *
     * @return The built SessionFactory.
     */
    SessionFactory build(Configuration conf);
}
