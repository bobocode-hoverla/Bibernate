package org.hoverla.bibernate.session;

import org.hoverla.bibernate.exception.datasource.JDBCConnectionException;
import org.hoverla.bibernate.exception.session.jdbc.PrepareStatementFailureException;
import org.hoverla.bibernate.fixtures.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EntityPersisterTest {
    private DataSource dataSource;
    private EntityPersister entityPersister;

    @BeforeEach
    public void setUp() throws NoSuchFieldException {
        dataSource = mock(DataSource.class);
        PersistenceContext persistenceContext = new PersistenceContext();
        entityPersister = new EntityPersister(dataSource, persistenceContext);
        Person.class.getDeclaredField("name").setAccessible(true);
        Person.class.getDeclaredField("age").setAccessible(true);
    }

    @Test
    void testInsert() throws SQLException {
        var entity = new Person(1, "John", 30);
        var connection = mockConnection();
        when(dataSource.getConnection()).thenReturn(connection);
        var result = entityPersister.insert(entity);
        assertEquals(entity, result);
        verify(connection).prepareStatement(anyString());
        verify(connection).close();
    }

    @Test
    void testInsertThrowsException() throws SQLException {
        var entity = new Person(1, "John", 30);
        when(dataSource.getConnection()).thenThrow(new SQLException("test"));
        assertThrows(JDBCConnectionException.class, () -> entityPersister.insert(entity));
    }

    @Test
    void testInsertThrowsPrepareStatementFailureException() throws SQLException {
        var entity = new Person(1, "John", 30);
        var connection = mockConnection();
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("test"));
        assertThrows(PrepareStatementFailureException.class, () -> entityPersister.insert(entity));
    }

    @Test
    void testUpdate() throws SQLException {
        var entity = new Person(1, "John", 30);
        var connection = mockConnection();
        when(dataSource.getConnection()).thenReturn(connection);
        entityPersister.update(entity);
        verify(connection).prepareStatement(anyString());
        verify(connection).close();
    }

    @Test
    void testUpdateThrowsException() throws SQLException {
        var entity = new Person(1, "John", 30);
        when(dataSource.getConnection()).thenThrow(new SQLException("test"));
        assertThrows(JDBCConnectionException.class, () -> entityPersister.update(entity));
    }

    @Test
    void testUpdateThrowsPrepareStatementFailureException() throws SQLException {
        var entity = new Person(1, "John", 30);
        var connection = mockConnection();
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("test"));
        assertThrows(PrepareStatementFailureException.class, () -> entityPersister.update(entity));
    }

    @Test
    void testDelete() throws SQLException {
        var entity = new Person(1, "John", 30);
        var connection = mockConnection();
        when(dataSource.getConnection()).thenReturn(connection);
        entityPersister.delete(entity);
        verify(connection).prepareStatement(anyString());
        verify(connection).close();
    }

    @Test
    void testDeleteThrowsException() throws SQLException {
        var entity = new Person(1, "John", 30);
        when(dataSource.getConnection()).thenThrow(new SQLException("test"));
        assertThrows(JDBCConnectionException.class, () -> entityPersister.delete(entity));
    }

    @Test
    void testFindById() throws Exception {
        var entity = new Person(1, "John Doe", 30);
        var connection = mockConnection();
        var resultSet = mockResultSet(entity);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));
        when(connection.prepareStatement(anyString()).executeQuery()).thenReturn(resultSet);
        var result = entityPersister.findById(entity.getClass(), 1L);
        assertEquals(entity, result);
        verify(connection, times(2)).prepareStatement(anyString());
        verify(connection).close();
    }

    @Test
    void testFindAllBy() throws Exception {
        var entity = new Person(1, "John Doe", 30);
        var connection = mockConnection();
        var resultSet = mockResultSet(entity);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));
        when(connection.prepareStatement(anyString()).executeQuery()).thenReturn(resultSet);
        var result = entityPersister.findAllBy(entity.getClass(), entity.getClass().getDeclaredField("name"), "John Doe");
        assertEquals(List.of(entity), result);
        verify(connection, times(2)).prepareStatement(anyString());
        verify(connection).close();
    }

    @Test
    void testFindOneBy() throws Exception {
        var entity = new Person(1, "John Doe", 30);
        var connection = mockConnection();
        var resultSet = mockResultSet(entity);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));
        when(connection.prepareStatement(anyString()).executeQuery()).thenReturn(resultSet);
        var result = entityPersister.findOneBy(entity.getClass(), entity.getClass().getDeclaredField("name"), "John Doe");
        assertEquals(entity, result);
        verify(connection, times(2)).prepareStatement(anyString());
        verify(connection).close();
    }

    private Connection mockConnection() throws SQLException {
        var connection = mock(Connection.class);
        var preparedStatement = mock(PreparedStatement.class);
        var resultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(resultSet.next()).thenReturn(true, false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        return connection;
    }

    private <T> ResultSet mockResultSet(T entity) throws SQLException {
        var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, false);
        Person person = (Person) entity;
        when(resultSet.getObject(anyString())).thenReturn(1, person.getName(), person.getAge());
        return resultSet;
    }
}

