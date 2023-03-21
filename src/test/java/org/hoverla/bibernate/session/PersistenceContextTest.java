package org.hoverla.bibernate.session;

import org.hoverla.bibernate.fixtures.Book;
import org.hoverla.bibernate.fixtures.Person;
import org.hoverla.bibernate.util.EntityKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextTest {

    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContext();
    }

    @Test
    void testManageEntity() {
        // Add an entity to the context
        var person = new Person(1, "John Doe", 30);
        var managedPerson = persistenceContext.manageEntity(person);

        // The managed entity should be the same as the original entity
        assertSame(person, managedPerson);

        // Add a different entity to the context
        var book = new Book(1,"The Catcher in the Rye");
        var managedBook = persistenceContext.manageEntity(book);

        // The managed entity should be the same as the original entity
        assertSame(book, managedBook);

        // Add the same entity to the context again
        var person2 = new Person(2, "Jane Doe", 30);
        var managedPerson2 = persistenceContext.manageEntity(person2);

        // The managed entity should be the same as the original entity
        assertSame(person2, managedPerson2);
    }

    @Test
    void testGetEntity() {
        // Add an entity to the context
        var person = new Person(1, "John Doe", 30);
        var managedPerson = persistenceContext.addEntity(person);

        // Get the entity from the context
        var entityKey = EntityKey.valueOf(managedPerson);
        var retrievedPerson = persistenceContext.getEntity(entityKey);

        // The retrieved entity should be the same as the original entity
        assertSame(managedPerson, retrievedPerson);
    }

    @Test
    void testContains() {
        // Add an entity to the context
        var person = new Person(1, "John Doe", 30);
        persistenceContext.addEntity(person);

        // Check if the context contains the entity
        assertTrue(persistenceContext.contains(person));

        // Check if the context contains a different entity
        var book = new Book(1, "The Catcher in the Rye");
        assertFalse(persistenceContext.contains(book));
    }

    @Test
    void testClear() {
        // Add entities to the context
        var person = new Person(1, "John Doe", 30);
        var book = new Book(1, "The Catcher in the Rye");
        persistenceContext.addEntity(person);
        persistenceContext.addEntity(book);

        // Clear the context
        persistenceContext.clear();

        // The context should be empty
        assertFalse(persistenceContext.contains(person));
        assertFalse(persistenceContext.contains(book));
    }
}

