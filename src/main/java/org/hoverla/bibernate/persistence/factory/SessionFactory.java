package org.hoverla.bibernate.persistence.factory;

public interface SessionFactory extends AutoCloseable {
    Session openSession();
}
