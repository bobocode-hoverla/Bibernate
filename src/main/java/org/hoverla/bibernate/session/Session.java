package org.hoverla.bibernate.session;

import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.Transaction;

public interface Session extends AutoCloseable {

    /**
     * Get the {@link Transaction} instance associated with this session.
     *
     * @return a Transaction instance
     * @throws IllegalStateException if session is already closed
     */
    Transaction getTransaction();

    /**
     * Make an instance managed and persistent.
     * @param entity  entity instance
     * @throws DuplicateEntityException if the entity is already present in persistence context
     * @throws IllegalStateException if session is already closed
     */
    <T> void persist(T entity);

    /**
     * Find by primary key.
     * Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence context,
     * it is returned from there.
     * @param entityType  entity class
     * @param id  primary key
     * @return the found entity instance or null if the entity does
     *         not exist
     * @throws SessionOperationException if the entity is not found,
     * connection has not been established, etc.
     * @throws IllegalStateException if session is already closed
     */
    <T> T find(Class<T> entityType, Object id);

    /**
     * Remove the entity instance.
     * @param entity  entity instance
     * @throws IllegalStateException if session is already closed
     */
    <T> void remove(T entity);

    /**
     * Synchronize the persistence context to the
     * underlying database.
     * @throws IllegalStateException if session is already closed
     */
    void flush();

    /**
     * Close the session by flushing all the changes to the underlying database,
     * cleaning up the persistence context
     * @throws IllegalStateException if session is already closed
     */
    void close();
}
