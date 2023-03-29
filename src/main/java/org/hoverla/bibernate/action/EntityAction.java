package org.hoverla.bibernate.action;

/**
 * Interface for actions relating to insert/update/delete of an entity instance.
 */
public interface EntityAction {
    void execute();

    int priority();
}
