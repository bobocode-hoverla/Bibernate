package org.hoverla.bibernate.util;

import static org.hoverla.bibernate.util.EntityUtils.getId;

/**
 * This class serves as an identifier for entity in session
 * Using it we can determine whether the entity already exists
 * in session scope
 * @param id entity id
 * @param type entity type
 */
public record EntityKey<T>(Object id, Class<T> type) {
    public static <T> EntityKey<T> valueOf(T entity) {
        var id = getId(entity);
        var type = (Class<T>) entity.getClass();
        return new EntityKey<>(id, type);
    }
}
