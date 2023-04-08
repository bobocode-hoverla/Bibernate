package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;

/**
 * Represents the priority of an entity action.
 */
@RequiredArgsConstructor
public enum EntityActionPriority {

    /**
     * Represents an entity insertion action with a priority of 1.
     */
    INSERT(1),

    /**
     * Represents an entity update action with a priority of 2.
     */
    UPDATE(2),

    /**
     * Represents an entity deletion action with a priority of 3.
     */
    DELETE(3);

    /**
     * The priority of the entity action.
     */
    private final int priority;

    /**
     * Gets the priority value of this entity action.
     *
     * @return the priority value of this entity action
     */
    public int getPriority() {
        return priority;
    }
}
