package org.hoverla.bibernate.session;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.util.EntityKey;

import java.util.HashMap;
import java.util.Map;

import static org.hoverla.bibernate.util.EntityUtils.mapToSnapshot;

/**
 * This class is serves as a first level cache of a current session.
 * All the entities are stored in a map to ensure that we don't query the
 * underlying database more times than it's actually needed
 */
@Slf4j
public class PersistenceContext {
    private final Map<EntityKey<?>, Object> entitiesMap = new HashMap<>();
    private final Map<EntityKey<?>, Object[]> snapshotCopiesMap = new HashMap<>();
    @Setter
    private boolean readonly;

    @SuppressWarnings("unchecked")
    public <T> T manageEntity(T entity) {
        log.trace("Checking entity {}", entity);
        var key = EntityKey.valueOf(entity);

        var cachedEntity = entitiesMap.get(key);
        if (cachedEntity != null) {
            log.trace("Entity is already in the context. Returning cached object {}", cachedEntity);
            return (T) cachedEntity;
        } else {
            return addEntity(entity);
        }
    }

    public <T> T getEntity(EntityKey<T> key) {
        log.trace("Getting entity from the context by key {}", key);
        Object entity = entitiesMap.get(key);
        return key.type().cast(entity);
    }

    public <T> T addEntity(T entity) {
        log.trace("Adding entity {} to the PersistenceContext", entity);
        var key = EntityKey.valueOf(entity);
        entitiesMap.put(key, entity);
        snapshotCopiesMap.put(key, mapToSnapshot(entity));
        return entity;
    }

    public boolean contains(Object entity) {
        log.trace("Checking if entity {} is present in persistence context", entity);
        var key = EntityKey.valueOf(entity);
        return entitiesMap.containsKey(key);
    }


    public void clear() {
        entitiesMap.clear();
        snapshotCopiesMap.clear();
    }
}
