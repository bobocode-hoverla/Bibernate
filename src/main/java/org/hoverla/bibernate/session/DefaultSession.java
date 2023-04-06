package org.hoverla.bibernate.session;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.action.EntityAction;
import org.hoverla.bibernate.action.EntityDeleteAction;
import org.hoverla.bibernate.action.EntityInsertAction;
import org.hoverla.bibernate.action.EntityUpdateAction;
import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.DelegatingDataSource;
import org.hoverla.bibernate.session.transaction.manager.TransactionManager;
import org.hoverla.bibernate.session.transaction.manager.TransactionManagerImpl;
import org.hoverla.bibernate.util.EntityKey;
import org.hoverla.bibernate.util.EntityUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparing;

@Slf4j
@RequiredArgsConstructor
public class DefaultSession implements Session {
    private final EntityPersister persister;
    private final PersistenceContext persistenceContext;
    private final Queue<EntityAction> actionQueue;
    private boolean closed;
    @Setter
    private boolean readonly;
    private TransactionManager transactionManager;

    public DefaultSession(DataSource dataSource) {
        this.actionQueue = new PriorityQueue<>(comparing(EntityAction::priority));
        this.persistenceContext = new PersistenceContext();

        this.transactionManager = new TransactionManagerImpl(dataSource, this);
        this.persister = new EntityPersister(dataSource, persistenceContext);
    }

    @Override
    public <T> void persist(T entity) {
        throwIfClosed();
        if (persistenceContext.contains(entity)) {
            throw new DuplicateEntityException(entity);
        }
        persistenceContext.addEntity(entity);
        var insertAction = new EntityInsertAction(entity, persister);
        actionQueue.add(insertAction);
    }

    @Override
    public <T> T merge(T entity) {
        throwIfClosed();
        log.info("Merging entity {} to persistence context", entity);
        if (persistenceContext.contains(entity)
            || this.find(entity.getClass(), EntityUtils.getId(entity)) != null) {
            var updateAction = new EntityUpdateAction(entity, persister);
            actionQueue.add(updateAction);
        } else {
            this.persist(entity);
        }
        return entity;
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        throwIfClosed();
        log.info("Finding entity {} by id = {}", entityType.getSimpleName(), id);
        try {
            return persister.findById(entityType, id);
        } catch (Exception e) {
            throw new SessionOperationException("Could not find entity by type: %s and id: %s"
                .formatted(entityType.getSimpleName(), id), e);
        }
    }

    @Override
    public <T> List<T> findAllBy(Class<T> entityType, String fieldName, Object value) {
        throwIfClosed();
        log.info("Finding entities {} by {} = {}", entityType.getSimpleName(), fieldName, value);
        try {
            Field field = EntityUtils.resolveFieldByName(entityType, fieldName);
            return persister.findAllBy(entityType, field, value);
        } catch (Exception e) {
                throw new SessionOperationException("Could not find entities by type: %s and %s: %s"
                    .formatted(entityType.getSimpleName(), fieldName, value), e);
            }
    }

    @Override
    public <T> T findOneBy(Class<T> entityType, String fieldName, Object value) {
        throwIfClosed();
        log.info("Finding entity {} by {} = {}", entityType.getSimpleName(), fieldName, value);
        try {
            Field field = EntityUtils.resolveFieldByName(entityType, fieldName);
            return persister.findOneBy(entityType, field, value);
        } catch (Exception e) {
            throw new SessionOperationException("Could not find entity by type: %s and %s: %s"
                .formatted(entityType.getSimpleName(), fieldName, value), e);
        }
    }

    @Override
    public <T> void remove(T entity) {
        throwIfClosed();
        log.info("Removing entity {}", entity);

        EntityKey<T> entityKey = EntityKey.valueOf(entity);
        var managedEntity = persistenceContext.getEntity(entityKey);
        if (managedEntity == null) {
            throw new IllegalArgumentException("Cannot remove entity %s. It's not present in a current session"
                .formatted(entityKey));
        }
        var removeAction = new EntityDeleteAction(entity, persister);
        actionQueue.add(removeAction);
    }

    @Override
    public void flush() {
        if (readonly) {
            log.warn("Readonly mode is ON. Dirty checking is not working");
        } else {
            log.trace("Flushing session");
            throwIfClosed();
            List<Object> dirtyEntities = persistenceContext.getDirtyEntities();
            dirtyEntities.forEach(dirtyEntity -> actionQueue.add(new EntityUpdateAction(dirtyEntity, persister)));
            while (!actionQueue.isEmpty()) {
                var entityAction = actionQueue.poll();
                entityAction.execute();
            }
        }
        persister.setDataSource(transactionManager.getRawDataSource());
    }

    @Override
    public void close() {
        log.info("Closing session");
        throwIfClosed();
        flush();
        persistenceContext.clear();
        transactionManager.clear();
        closed = true;
    }


    private void throwIfClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Session is closed. Please open a new one!");
        }
    }

    public boolean isClosed() {
        return closed;
    }
    @Override
    public TransactionManager getTransactionManager() {
        persister.setDataSource(new DelegatingDataSource(transactionManager.getRawDataSource(), transactionManager));
        return transactionManager;
    }
}
