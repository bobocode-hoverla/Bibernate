package org.hoverla.bibernate.action;

/**
 * Represents an action to be executed on an entity.
 */
public interface EntityAction {

    /**
     * Executes the action.
     */
    void execute();

    /**
     * Gets the priority of the action. Actions with higher priority values will be executed first.
     *
     * @return the priority of the action
     */
    int priority();
}
