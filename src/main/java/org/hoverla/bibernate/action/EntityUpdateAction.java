package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.session.EntityPersister;

@Slf4j
@RequiredArgsConstructor
public class EntityUpdateAction implements EntityAction {

    private final Object entity;
    private final EntityPersister persister;

    /**
     * Updates the entity using the entity persister.
     */
    @Override
    public void execute() {
        log.debug("Executing EntityUpdateAction for entity: {}", entity);
        persister.update(entity);
        log.debug("EntityUpdateAction completed for entity: {}", entity);
    }

    /**
     * Gets the priority of the entity update action.
     *
     * @return the priority of the entity update action
     */
    @Override
    public int priority() {
        return EntityActionPriority.UPDATE.getPriority();
    }
}