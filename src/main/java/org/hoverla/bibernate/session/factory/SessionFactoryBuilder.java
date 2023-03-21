package org.hoverla.bibernate.session.factory;

import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.session.Session;

/**
 * The contract for building a {@link Session} given a configuration.
 */
public interface SessionFactoryBuilder {
    /**
     * Builds the SessionFactory.
     *
     * @return The built SessionFactory.
     */
    SessionFactory build(Configuration conf);
}
