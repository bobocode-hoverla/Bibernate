package org.hoverla.bibernate.persistence.factory;

import org.hoverla.bibernate.persistence.session.Session;

public interface SessionFactory extends AutoCloseable {
    Session openSession();
}
