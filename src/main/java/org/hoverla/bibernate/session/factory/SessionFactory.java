package org.hoverla.bibernate.session.factory;


import org.hoverla.bibernate.session.Session;

public interface SessionFactory extends AutoCloseable {
    Session openSession();
}
