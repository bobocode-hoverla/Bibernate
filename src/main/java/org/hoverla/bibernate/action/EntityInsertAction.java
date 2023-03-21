package org.hoverla.bibernate.action;

import lombok.RequiredArgsConstructor;
import org.hoverla.bibernate.session.EntityPersister;

@RequiredArgsConstructor
public class EntityInsertAction implements EntityAction {
    private final Object entity;
    private final EntityPersister persister;

    @Override
    public void execute() {
        persister.insert(entity);
    }

    @Override
    public int priority() {
        return EntityActionPriority.INSERT.getPriority();
    }
}
