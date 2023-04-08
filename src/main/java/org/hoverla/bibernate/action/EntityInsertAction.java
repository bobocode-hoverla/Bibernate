package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.session.EntityPersister;

@Slf4j
@RequiredArgsConstructor
public class EntityInsertAction implements EntityAction {

    private final Object entity;
    private final EntityPersister persister;

    /**
     * Inserts the entity using the entity persister.
     */
    @Override
    public void execute() {
        log.debug("Executing EntityInsertAction for entity: {}", entity);
        persister.insert(entity);
        log.debug("EntityInsertAction completed for entity: {}", entity);
    }

    /**
     * Gets the priority of the entity insertion action.
     *
     * @return the priority of the entity insertion action
     */
    @Override
    public int priority() {
        return EntityActionPriority.INSERT.getPriority();
    }
}
