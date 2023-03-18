package org.hoverla.bibernate.persistence.session;

import org.hoverla.bibernate.persistence.transaction.Transaction;

/**
 * Represents a session with a database.
 * <p>
 * A session can be used to manage a set of database operations within the context of a
 * single connection to the database. To use a session, first call the beginTransaction()
 * method to start a transaction. Then, perform the necessary database operations within
 * the context of the transaction. Once all the operations have completed successfully,
 * call the commit() method on the transaction to commit the changes to the database. If an
 * error occurs, call the rollback() method on the transaction to undo any changes made
 * within the transaction. Finally, call the close() method on the session to release the
 * resources used by the session.
 */
public interface Session  {
    /**
     * Begins a transaction on this session.
     *
     * @return the transaction object that can be used to manage the transaction.
     */
    Transaction beginTransaction();

    /**
     * Get the {@link org.hoverla.bibernate.persistence.transaction.Transaction} instance associated with this session.
     *
     * @return a Transaction instance
     */
    Transaction getTransaction();

    /**
     * Closes this session and releases any resources used by the session.
     */
    void close();
}
