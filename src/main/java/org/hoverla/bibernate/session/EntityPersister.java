package org.hoverla.bibernate.session;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.collection.LazyList;
import org.hoverla.bibernate.exception.datasource.JDBCConnectionException;
import org.hoverla.bibernate.exception.session.jdbc.PrepareStatementFailureException;
import org.hoverla.bibernate.util.EntityKey;
import org.hoverla.bibernate.util.EntityUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.hoverla.bibernate.util.EntityUtils.*;
import static org.hoverla.bibernate.util.SqlUtils.*;


/**
 * This class is used for JDBC operations within the Session
 * Typically invoked during the flush/commit/close operations
 */
@Slf4j
@AllArgsConstructor
public class EntityPersister {
    @Setter
    private DataSource dataSource;
    private final PersistenceContext persistenceContext;

    private static final String TABLE_LOG = "Resolved table name -> {}";
    private static final String CONNECTION_ERROR = "Unable to acquire JDBC Connection";

    public <T> T insert(T entity) {
        log.trace("Inserting entity {}", entity);
        var type = entity.getClass();
        try (var conn = dataSource.getConnection()) {
            var table = resolveTableName(type);
            log.trace(TABLE_LOG, table);
            var columns = getCommaSeparatedInsertableColumns(type);
            var params = getCommaSeparatedInsertableParams(type);
            var insertQuery = INSERT_TEMPLATE.formatted(table, columns, params);
            log.trace("Insert query: {}", insertQuery);
            executeInsert(entity, conn, insertQuery);
        } catch (SQLException e) {
            log.error(CONNECTION_ERROR, e);
            throw new JDBCConnectionException(e);
        }
        return entity;
    }

    public <T> void update(T entity) {
        log.trace("Updating entity {}", entity);
        var type = entity.getClass();
        try (var conn = dataSource.getConnection()) {
            var table = resolveTableName(type);
            log.trace(TABLE_LOG, table);
            var columns = getCommaSeparatedUpdatableColumns(type);
            var idField = getIdField(type);
            var idColumnName = resolveColumnName(idField) + " = ? ";
            log.trace("Resolved id column name -> {}", idColumnName);
            String updateQuery = UPDATE_TEMPLATE.formatted(table, columns, idColumnName);
            executeUpdate(entity, conn, updateQuery);
        } catch (SQLException e) {
            log.error(CONNECTION_ERROR, e);
            throw new JDBCConnectionException(e);
        }
    }

    public <T> void delete(T entity) {
        log.trace("Removing entity {}", entity);
        var type = entity.getClass();
        try (var conn = dataSource.getConnection()) {
            var table = resolveTableName(type);
            log.trace(TABLE_LOG, table);
            var idField = getIdField(type);
            var idColumnName = resolveColumnName(idField);
            log.trace("Resolved id column name -> {}", idColumnName);
            var id = getId(entity);
            String deleteQuery = DELETE_BY_COLUMN_TEMPLATE.formatted(table, idColumnName);
            try (var deleteStatement = conn.prepareStatement(deleteQuery)) {
                deleteStatement.setObject(1, id);
                log.debug("SQL: {}", deleteStatement);
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(CONNECTION_ERROR, e);
            throw new JDBCConnectionException(e);
        }
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
            log.trace(TABLE_LOG, tableName);
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
            } else if (isSingleObjectField(field)) {
                log.trace("Processing single object id");
                var relatedEntityType = field.getType();
                var fieldName = resolveColumnName(field);
                var relatedFieldValue = resultSet.getObject(fieldName);
                var relatedIdField = getIdField(relatedEntityType);
                var relatedEntity = findOneBy(relatedEntityType, relatedIdField, relatedFieldValue);
                field.set(entity, relatedEntity);
            } else if (isMultipleObjectField(field)) {
                log.trace("Processing collection object");
                var parameterizedType = (ParameterizedType) field.getGenericType();
                var parametrizedTypes = parameterizedType.getActualTypeArguments();
                var relatedEntityType = (Class<?>) parametrizedTypes[0];

                var relatedEntityField = getRelatedEntityField(entityType, relatedEntityType);
                var entityId = getId(entity);

                Supplier<List<?>> relatedEntityCollectionSupplier =
                        () -> {
                            try {
                                return findAllBy(relatedEntityType, relatedEntityField, entityId);
                            } catch (Exception e) {
                                throw new RuntimeException("Can not find all by for %s" + relatedEntityType);
                            }
                        };
                var collectionType = new LazyList<T>(relatedEntityCollectionSupplier);
                field.set(entity, collectionType);
            } else if (isRegularField(field)) {
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
            log.debug("SQL: " + insertQuery);
            insertStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            log.error("Could not prepare statement with SQL: {}", insertQuery, e);
            throw new PrepareStatementFailureException(insertQuery, e);
        }
    }

    private <T> void executeUpdate(T entity, Connection conn, String updateQuery) {
        try (var updateStatement = conn.prepareStatement(updateQuery)) {
            fillUpdateStatementParams(updateStatement, entity);
            var idParamIndex = EntityUtils.getFieldsForUpdate(entity.getClass()).length + 1;
            updateStatement.setObject(idParamIndex, getId(entity));
            log.debug("SQL: " + updateStatement);
            updateStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            log.error("Could not prepare statement with SQL: {}", updateQuery, e);
            throw new PrepareStatementFailureException(updateQuery, e);
        }
    }

    private void fillInsertStatementParams(PreparedStatement insertStatement, Object entity)
            throws IllegalAccessException, SQLException {
        Field[] fieldsForInsert = getFieldsForInsert(entity.getClass());
        prepareStatement(insertStatement, entity, fieldsForInsert);
    }

    private void fillUpdateStatementParams(PreparedStatement updateStatement, Object entity)
            throws IllegalAccessException, SQLException {
        Field[] fieldsForInsert = getFieldsForUpdate(entity.getClass());
        prepareStatement(updateStatement, entity, fieldsForInsert);
    }

    @SuppressWarnings("java:S3011")
    private static void prepareStatement(PreparedStatement statement, Object entity, Field[] fieldsForInsert) throws IllegalAccessException, SQLException {
        for (int i = 0; i < fieldsForInsert.length; i++) {
            var f = fieldsForInsert[i];
            f.setAccessible(true);
            Object columnValue = f.get(entity);
            statement.setObject(i + 1, columnValue);
        }
    }
}
