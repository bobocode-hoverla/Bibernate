package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.session.EntityPersister;


@Slf4j
@RequiredArgsConstructor
public class EntityDeleteAction implements EntityAction {

    private final Object entity;
    private final EntityPersister persister;

    /**
     * Deletes the entity using the entity persister.
     */
    @Override
    public void execute() {
        log.debug("Executing EntityDeleteAction for entity: {}", entity);
        persister.delete(entity);
        log.debug("EntityDeleteAction completed for entity: {}", entity);
    }

    /**
     * Gets the priority of the entity deletion action.
     *
     * @return the priority of the entity deletion action
     */
    @Override
    public int priority() {
        return EntityActionPriority.DELETE.getPriority();
    }
}
