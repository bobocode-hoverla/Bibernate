package org.hoverla.bibernate.session;

import org.hoverla.bibernate.connectionpool.pool.WrapperConnection;
import org.hoverla.bibernate.fixtures.Person;
import org.hoverla.bibernate.exception.session.DuplicateEntityException;
import org.hoverla.bibernate.exception.session.SessionOperationException;
import org.hoverla.bibernate.session.transaction.manager.TransactionManager;
import org.hoverla.bibernate.util.EntityKey;
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
import static org.mockito.Mockito.*;

class DefaultSessionTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PersistenceContext persistenceContext;

    private DefaultSession defaultSession;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);
        ThreadLocal<Connection> connections = new ThreadLocal<>();
        connections.set(new WrapperConnection(null, null));
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
        var retrievedPerson = defaultSession.find(Person.class, person.getId());
        Assertions.assertEquals(person, retrievedPerson);
    }

    @Test
    void testMerge() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO person(name, age) VALUES(?,?);")).thenReturn(ps);
        var person = new Person(ThreadLocalRandom.current().nextInt(), "John", 30);
        defaultSession.persist(person);
        person.setName("Name new");
        var retrievedPerson = defaultSession.merge(person);
        Assertions.assertEquals(person, retrievedPerson);
    }

    @Test
    void testPersistDuplicate() {
        var person = new Person(ThreadLocalRandom.current().nextInt(), "John", 30);
        defaultSession.persist(person);
        Assertions.assertThrows(DuplicateEntityException.class, () -> defaultSession.persist(person));
    }

    @Test
    void testFindByColumn() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        var person = new Person(1, "James", 30);
        defaultSession.persist(person);
        ResultSet resultSet = mockResultSet(person);
        when(ps.executeQuery()).thenReturn(resultSet);
        var retrievedPerson = defaultSession.findOneBy(Person.class, "name", "James");
        Assertions.assertEquals(person, retrievedPerson);
    }

    @Test
    void testFindAllByColumn() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        var person = new Person(1, "James", 30);
        var nextPerson = new Person(2, "James", 25);
        defaultSession.persist(person);
        defaultSession.persist(nextPerson);
        ResultSet resultSet = mockResultSet(person, nextPerson);
        when(ps.executeQuery()).thenReturn(resultSet);
        var retrievedPersons = defaultSession.findAllBy(Person.class, "name", "James");

        Assertions.assertTrue(retrievedPersons.contains(person));
        Assertions.assertTrue(retrievedPersons.contains(nextPerson));

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
    void testFindOneNonExistentEntity() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT * FROM person WHERE id = ?;")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        Assertions.assertThrows(SessionOperationException.class, () -> defaultSession.findOneBy(Person.class, "name", "name"));
    }

    @Test
    void testFindAllNonExistentEntity() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT * FROM person WHERE id = ?;")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        Assertions.assertThrows(SessionOperationException.class, () -> defaultSession.findAllBy(Person.class, "name", "name"));
    }

    @Test
    void testRemove() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO person(name, age) VALUES(?,?);")).thenReturn(ps);
        var person = new Person(1, "John", 30);
        defaultSession.persist(person);
        var key = EntityKey.valueOf(person);
        when(persistenceContext.getEntity(key)).thenReturn(person);
        when(connection.prepareStatement("DELETE FROM person WHERE id = ?;")).thenReturn(ps);
        defaultSession.remove(person);
        Assertions.assertFalse(persistenceContext.contains(person));
    }

    @Test
    void testRemoveNotInSession() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement ps = mock(PreparedStatement.class);
        var person = new Person(1, "John", 30);
        var key = EntityKey.valueOf(person);
        when(persistenceContext.getEntity(key)).thenReturn(person);
        when(connection.prepareStatement("DELETE FROM person WHERE id = ?;")).thenReturn(ps);
        Assertions.assertThrows(IllegalArgumentException.class, () -> defaultSession.remove(person));
    }

    @Test
    void testGetTransactionManager() {
        TransactionManager transactionManager = defaultSession.getTransactionManager();
        assertNotNull(transactionManager);
    }

    @Test
    void testFlush() throws SQLException {
        int id = ThreadLocalRandom.current().nextInt();
        var person = new Person(id, "John", 30);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO person(name, age) VALUES(?,?);")).thenReturn(ps);
        defaultSession.persist(person);
        Assertions.assertEquals(person, defaultSession.find(Person.class, id));
    }

    @Test
    void testClose() {
        defaultSession.close();
        Assertions.assertTrue(defaultSession.isClosed());
    }

    private <T> ResultSet mockResultSet(T entity) throws SQLException {
        var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, false);
        Person person = (Person) entity;
        when(resultSet.getObject(anyString())).thenReturn(1, person.getName(), person.getAge());
        return resultSet;
    }

    private <T> ResultSet mockResultSet(T entity, T anotherEntity) throws SQLException {
        var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false);
        Person person = (Person) entity;
        Person nextPerson = (Person) anotherEntity;
        when(resultSet.getObject(anyString())).thenReturn(1, person.getName(), person.getAge())
            .thenReturn(2, nextPerson.getName(), nextPerson.getAge());
        return resultSet;
    }
}