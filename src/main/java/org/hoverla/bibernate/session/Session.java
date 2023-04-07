package org.hoverla.bibernate.session;

//import jakarta.persistence.TransactionRequiredException;
import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.manager.TransactionManager;

import java.util.List;

public interface Session extends AutoCloseable {

    /**
     * Get the {@link TransactionManager} instance associated with this session.
     *
     * @return a TransactionManager instance
     * @throws IllegalStateException if session is already closed
     */
    TransactionManager getTransactionManager();

    /**
     * Make an instance managed and persistent.
     * @param entity  entity instance
     * @throws DuplicateEntityException if the entity is already present in persistence context
     * @throws IllegalStateException if session is already closed
     */
    <T> void persist(T entity);

    /**
     * Merge the state of the given entity into the
     * current persistence context.
     * @param entity  entity instance
     * @return the managed instance that the state was merged to
     * @throws IllegalArgumentException if instance is not an
     *         entity or is a removed entity
     * @throws TransactionRequiredException if there is no transaction when
     *         invoked on a container-managed entity manager of that is of type
     *         <code>PersistenceContextType.TRANSACTION</code>
     */
    <T> T merge(T entity);

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
     * Find by field.
     * Search for entities of the specified class and one of its fields.
     * If there are entities contained in the persistence context,
     * they are returned from there.
     * @param entityType  entity class
     * @param fieldName  name of the field
     * @param value value of the field
     * @return the found entity instances list or empty collection
     * @throws SessionOperationException if connection has not been established, etc.
     * @throws IllegalStateException if session is already closed
     */
    <T> List<T> findAllBy(Class<T> entityType, String fieldName, Object value);

    /**
     * Find by field.
     * Search for an entity of the specified class and one of its fields.
     * If there is entity contained in the persistence context,
     * it is returned from there.
     * @param entityType  entity class
     * @param fieldName  name of the field
     * @param value value of the field
     * @return the found entity instance
     * @throws SessionOperationException if connection has not been established, etc.
     * @throws IllegalStateException if session is already closed
     */
    <T> T findOneBy(Class<T> entityType, String fieldName, Object value);

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

    /**
     * marks the session readonly (dirty checking not working)
     * @param readonly readonly flag
     */
    void setReadonly(boolean readonly);
}
