package org.hoverla.bibernate.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.exception.datasource.JDBCConnectionException;
import org.hoverla.bibernate.exception.session.jdbc.PrepareStatementFailureException;
import org.hoverla.bibernate.util.EntityKey;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hoverla.bibernate.util.EntityUtils.getFieldsForInsert;
import static org.hoverla.bibernate.util.EntityUtils.getIdField;
import static org.hoverla.bibernate.util.EntityUtils.isColumnField;
import static org.hoverla.bibernate.util.EntityUtils.isIdField;
import static org.hoverla.bibernate.util.EntityUtils.resolveColumnName;
import static org.hoverla.bibernate.util.EntityUtils.resolveColumnValue;
import static org.hoverla.bibernate.util.EntityUtils.resolveTableName;
import static org.hoverla.bibernate.util.SqlUtils.INSERT_TEMPLATE;
import static org.hoverla.bibernate.util.SqlUtils.SELECT_BY_COLUMN_TEMPLATE;
import static org.hoverla.bibernate.util.SqlUtils.getCommaSeparatedInsertableColumns;
import static org.hoverla.bibernate.util.SqlUtils.getCommaSeparatedInsertableParams;


/**
 * This class is used for JDBC operations within the Session
 * Typically invoked during the flush/commit/close operations
 */
@Slf4j
@RequiredArgsConstructor
public class EntityPersister {
    private final DataSource dataSource;
    private final PersistenceContext persistenceContext;

    public <T> T insert(T entity) {
        log.trace("Inserting entity {}", entity);
        var type = entity.getClass();
        try (var conn = dataSource.getConnection()) {
            var table = resolveTableName(type);
            var columns = getCommaSeparatedInsertableColumns(type);
            var params = getCommaSeparatedInsertableParams(type);
            var insertQuery = INSERT_TEMPLATE.formatted(table, columns, params);
            log.trace("Insert query: {}", insertQuery);
            executeInsert(entity, conn, insertQuery);
        } catch (SQLException e) {
            log.error("Unable to acquire JDBC Connection", e);
            throw new JDBCConnectionException(e);
        }
        return entity;
    }

    public <T> T findById(Class<T> entityType, Object id) throws SQLException,
        InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        log.trace("Selecting entity {} by id = {}", entityType.getSimpleName(), id);
        var key = new EntityKey<>(id, entityType);
        var cachedEntity = persistenceContext.getEntity(key);
        if (cachedEntity != null) {
            log.trace("Returning cached entity from the context {}", cachedEntity);
            return entityType.cast(cachedEntity);
        }
        log.trace("No cached entity found... Loading entity from the DB");
        var idField = getIdField(entityType);
        return findOneBy(entityType, idField, id);
    }

    public <T> List<T> findAllBy(Class<T> entityType, Field field, Object columnValue) throws SQLException,
        InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        log.trace("Selecting from table by column value");
        var list = new ArrayList<T>();
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(entityType);
            log.trace("Resolved table name -> {}", tableName);
            var columnName = resolveColumnName(field);
            log.trace("Resolved column name -> {}", columnName);
            var selectSql = String.format(SELECT_BY_COLUMN_TEMPLATE, tableName, columnName);
            log.trace("Preparing select statement: {}", selectSql);
            try (var selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setObject(1, columnValue);
                log.debug("SQL: {}", selectStatement);
                var resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    var entity = createEntityFrom(entityType, resultSet);
                    list.add(entity);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("java:S3011")
    private <T> T createEntityFrom(Class<T> entityType, ResultSet resultSet) throws NoSuchMethodException,
        InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        log.trace("Creating entity {} from the result set", entityType.getSimpleName());
        var constructor = entityType.getConstructor();
        var entity = constructor.newInstance();
        log.trace("Processing entity fields");
        for (var field : entityType.getDeclaredFields()) {
            field.setAccessible(true);
            if (isIdField(field)) {
                log.trace("Processing id field {}", field.getName());
                var idField = getIdField(entityType);
                var columnName = resolveColumnName(idField);
                log.trace("Resolved id column name '{}'", columnName);
                var id = resultSet.getObject(columnName);
                log.trace("Setting value '{}' to the entity id", id);
                field.set(entity, id);
            } else if (isColumnField(field)) {
                log.trace("Processing simple field {}", field.getName());
                var columnName = resolveColumnName(field);
                log.trace("Resolved column name '{}'", columnName);
                var columnValue = resolveColumnValue(resultSet.getObject(columnName));
                log.trace("Fetched column value '{}' from the result set", columnValue);
                log.trace("Setting value '{}' to the entity field {}", columnValue, field.getName());
                field.set(entity, columnValue);
            }
        }
        return persistenceContext.manageEntity(entity);
    }

    public <T> T findOneBy(Class<T> entityType, Field field, Object columnValue) throws SQLException,
        InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var result = findAllBy(entityType, field, columnValue);
        if (result.size() != 1) {
            throw new IllegalStateException("The result must contain exactly one row");
        }
        return result.get(0);
    }

    private <T> void executeInsert(T entity, Connection conn, String insertQuery) {
        try (var insertStatement = conn.prepareStatement(insertQuery)) {
            fillInsertStatementParams(insertStatement, entity);
            insertStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            log.error("Could not prepare statement with SQL: {}", insertQuery, e);
            throw new PrepareStatementFailureException(insertQuery, e);
        }
    }

    @SuppressWarnings("java:S3011")
    private void fillInsertStatementParams(PreparedStatement insertStatement, Object entity)
        throws IllegalAccessException, SQLException {
        Field[] fieldsForInsert = getFieldsForInsert(entity.getClass());
        for (int i = 0; i < fieldsForInsert.length; i++) {
            var f = fieldsForInsert[i];
            f.setAccessible(true);
            Object columnValue = f.get(entity);
            insertStatement.setObject(i + 1, columnValue);
        }

    }

    public void update(Object entity) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public void delete(Object entity) {
        //TODO
        throw new UnsupportedOperationException();
    }
}
