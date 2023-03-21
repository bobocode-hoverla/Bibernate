package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;
import org.hoverla.bibernate.session.EntityPersister;

@RequiredArgsConstructor
public class EntityDeleteAction implements EntityAction {
    private final Object entity;
    private final EntityPersister persister;

    @Override
    public void execute() {
        persister.delete(entity);
    }

    @Override
    public int priority() {
        return EntityActionPriority.DELETE.getPriority();
    }
}
