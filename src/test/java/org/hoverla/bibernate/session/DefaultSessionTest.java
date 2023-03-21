package org.hoverla.bibernate.session;

import org.hoverla.bibernate.fixtures.Person;
import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DefaultSessionTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private DefaultSession defaultSession;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        defaultSession = new DefaultSession(dataSource);
    }

    @Test
    void testPersistAndFind() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO person(name, age) VALUES(?,?);")).thenReturn(ps);
        var person = new Person(ThreadLocalRandom.current().nextInt(), "John", 30);
        defaultSession.persist(person);
        defaultSession.flush();
        var retrievedPerson = defaultSession.find(Person.class, person.getId());
        Assertions.assertEquals(person, retrievedPerson);
    }

    @Test
    void testPersistDuplicate() {
        var person = new Person(ThreadLocalRandom.current().nextInt(), "John", 30);
        defaultSession.persist(person);
        Assertions.assertThrows(DuplicateEntityException.class, () -> defaultSession.persist(person));
    }

    @Test
    void testFindNonExistentEntity() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT * FROM person WHERE id = ?;")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        Assertions.assertThrows(SessionOperationException.class, () -> defaultSession.find(Person.class, 34235));
    }

    @Test
    void testGetTransaction() {
        Transaction transaction = defaultSession.getTransaction();
        assertNotNull(transaction);
        assertTrue(transaction.isClosed());
    }

    @Test
    void testFlush() throws SQLException {
        int id = ThreadLocalRandom.current().nextInt();
        var person = new Person(id, "John", 30);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO person(name, age) VALUES(?,?);")).thenReturn(ps);
        defaultSession.persist(person);
        defaultSession.flush();
        Assertions.assertEquals(person, defaultSession.find(Person.class, id));
    }

    @Test
    void testClose() {
        defaultSession.close();
        Assertions.assertTrue(defaultSession.isClosed());
    }
}