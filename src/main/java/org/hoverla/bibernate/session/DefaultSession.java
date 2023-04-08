package org.hoverla.bibernate.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.action.EntityAction;
import org.hoverla.bibernate.action.EntityInsertAction;
import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.datasource.JDBCConnectionException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.Transaction;
import org.hoverla.bibernate.session.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparing;

/**
 DefaultSession is an implementation of the Session interface that provides basic CRUD
 operations for interacting with entities.

 This implementation uses an EntityPersister to persist and retrieve entities,
 and a PersistenceContext to keep track of entities managed by the session.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSession implements Session {
    private final EntityPersister persister;
    private final PersistenceContext persistenceContext;
    private final Queue<EntityAction> actionQueue;
    private boolean closed;
    private Transaction transaction;
    private TransactionFactory transactionFactory;

    /**
     Constructs a new DefaultSession object with the given DataSource.
     @param dataSource the DataSource to use for creating the EntityPersister and TransactionFactory objects
     */
    public DefaultSession(DataSource dataSource) {
        actionQueue = new PriorityQueue<>(comparing(EntityAction::priority));
        persistenceContext = new PersistenceContext();
        persister = new EntityPersister(dataSource, persistenceContext);
        transactionFactory = new TransactionFactory(dataSource);
        try {
            transaction = transactionFactory.getTransactionForSession(this);
        } catch (SQLException e) {
            throw new JDBCConnectionException(e);
        }
    }

    @Override
    public Transaction getTransaction() {
        throwIfClosed();
        if (transaction != null && !transaction.isClosed()) {
            log.warn("Transaction is already opened. All following operations will be performed within the existing transaction.");
            return transaction;
        }

        try {
            transaction = transactionFactory.getTransactionForSession(this);
            return transaction;
        } catch (SQLException e) {
            throw new JDBCConnectionException(e);
        }
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
    public <T> void remove(T person) {
        throwIfClosed();
    }

    @Override
    public void flush() {
        log.trace("Flushing session");
        throwIfClosed();
        while (!actionQueue.isEmpty()) {
            var entityAction = actionQueue.poll();
            entityAction.execute();
        }
    }

    @Override
    public void close() {
        log.info("Closing session");
        throwIfClosed();
        flush();
        persistenceContext.clear();
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

    public void setReadonly(boolean readonly) {
        persistenceContext.setReadonly(readonly);
    }
}
