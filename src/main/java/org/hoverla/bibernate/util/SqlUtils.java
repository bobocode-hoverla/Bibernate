package org.hoverla.bibernate.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hoverla.bibernate.util.EntityUtils.getFieldsForInsert;
import static org.hoverla.bibernate.util.EntityUtils.getFieldsForUpdate;

/**
 * This class serves as a helper class for creating various sql queries
 */
@UtilityClass
public class SqlUtils {
    public static final String INSERT_TEMPLATE = "INSERT INTO %s(%s) VALUES(%s);";
    public static final String SELECT_BY_COLUMN_TEMPLATE = "SELECT * FROM %s WHERE %s = ?;";
    public static final String DELETE_BY_COLUMN_TEMPLATE = "DELETE FROM %s WHERE %s = ?;";
    public static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s;";


    public static String getCommaSeparatedInsertableColumns(Class<?> entityType) {
        var insertableFields = getFieldsForInsert(entityType);
        return Arrays.stream(insertableFields)
            .map(EntityUtils::resolveColumnName)
            .collect(Collectors.joining(", "));
    }

    public static String getCommaSeparatedUpdatableColumns(Class<?> entityType) {
        var updatableFields = getFieldsForUpdate(entityType);
        return Arrays.stream(updatableFields)
            .map(EntityUtils::resolveColumnName)
            .map(columnName -> columnName + " = ?")
            .collect(Collectors.joining(", "));
    }


    public static String getCommaSeparatedInsertableParams(Class<?> entityType) {
        var insertableFields = getFieldsForInsert(entityType);
        return Arrays.stream(insertableFields)
            .map(f -> "?")
            .collect(Collectors.joining(","));
    }
}
