package org.hoverla.bibernate.util;

import org.hoverla.bibernate.annotation.Column;
import org.hoverla.bibernate.annotation.Entity;
import org.hoverla.bibernate.annotation.Id;
import org.hoverla.bibernate.annotation.Table;
import org.hoverla.bibernate.annotation.relations.ManyToMany;
import org.hoverla.bibernate.annotation.relations.ManyToOne;
import org.hoverla.bibernate.annotation.relations.OneToMany;
import org.hoverla.bibernate.annotation.relations.OneToOne;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class provides a utility for generating a CREATE TABLE statement in SQL
 * for a given Java entity class. The class uses reflection to inspect the fields
 * of the entity class and generate a CREATE TABLE statement based on the
 * annotations present on each field.
 */
public class DdlGenerator {
    private final QueryExecutor queryExecutor;

    public DdlGenerator(DataSource dataSource) {
        this.queryExecutor = new QueryExecutor(dataSource);
    }


    /**
     Generates the DDL (Data Definition Language) scripts for all
     entities in the specified package that are annotated with {@link Entity} annotation.

     @param entitiesPackage the package name where the entities are located.
     @throws IllegalArgumentException if the provided package name is null or empty.
     */
    public void generateDdl(String entitiesPackage) {
        Reflections reflections = new Reflections(entitiesPackage);
        var entities = reflections.getTypesAnnotatedWith(Entity.class);
        entities.forEach(this::generateDDL);
    }

    private void generateDDL(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        String tableName = getTableName(clazz);
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        List<Field> fields = getAllFields(clazz);
        List<Field> primaryKeyFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                primaryKeyFields.add(field);
            }
            sb.append(getColumnName(field)).append(" ").append(getColumnType(field.getType()));
            if (field.isAnnotationPresent(Id.class)) {
                sb.append(" SERIAL PRIMARY KEY");
            }
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (column.unique()) {
                    sb.append(" UNIQUE");
                }
                if (!column.nullable()) {
                    sb.append(" NOT NULL");
                }
            }
            sb.append(", ");
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                Class<?> targetEntityClass = oneToMany.targetEntity();
                String targetTableName = getTableName(targetEntityClass);
                String targetFieldName = oneToMany.mappedBy();
                Field targetField = getField(targetEntityClass, targetFieldName);
                sb.append("FOREIGN KEY (").append(getColumnName(field)).append(") REFERENCES ")
                        .append(targetTableName).append("(").append(getColumnName(targetField)).append(")");
                sb.append(", ");
            } else if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                Class<?> targetEntityClass = field.getType();
                String targetTableName = getTableName(targetEntityClass);
                Field targetField = getField(targetEntityClass, manyToOne.mappedBy());
                sb.append("FOREIGN KEY (").append(getColumnName(field)).append(") REFERENCES ")
                        .append(targetTableName).append("(").append(getColumnName(targetField)).append(")");
                sb.append(", ");
            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                Class<?> targetEntityClass = manyToMany.targetEntity();
                String targetTableName = getTableName(targetEntityClass);
                String joinTableName = getJoinTableName(tableName, targetTableName);
                sb.append("FOREIGN KEY (").append(getColumnName(field)).append(") REFERENCES ")
                        .append(tableName).append("(").append(getColumnName(primaryKeyFields.get(0))).append(")");
                sb.append(", FOREIGN KEY (").append(manyToMany.mappedBy()).append(") REFERENCES ")
                        .append(targetTableName).append("(").append(getColumnName(primaryKeyFields.get(0))).append(")");
                sb.append(", CONSTRAINT ").append(joinTableName).append("_pk PRIMARY KEY (")
                        .append(getColumnName(primaryKeyFields.get(0))).append(", ")
                        .append(manyToMany.mappedBy()).append(")");
                sb.append(", ");
            } else if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                Class<?> targetEntityClass = oneToOne.targetEntity();
                String targetTableName = getTableName(targetEntityClass);
                String targetFieldName = oneToOne.mappedBy();
                Field targetField = getField(targetEntityClass, targetFieldName);
                sb.append("FOREIGN KEY (").append(getColumnName(field)).append(") REFERENCES ")
                        .append(targetTableName).append("(").append(getColumnName(targetField)).append(")");
                sb.append(", ");
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        String sql = sb.toString();
        queryExecutor.execute(sql);
    }

    private static String getTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            return table.name();
        } else {
            return clazz.getSimpleName().toLowerCase();
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            } else {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            }
        }
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.name();
        } else {
            return field.getName();
        }
    }

    private static String getJoinTableName(String tableName1, String tableName2) {
        List<String> tableNames = Arrays.asList(tableName1, tableName2);
        // sort the table names to ensure consistency in naming
        Collections.sort(tableNames);
        StringBuilder sb = new StringBuilder();
        for (String tableName : tableNames) {
            sb.append(tableName).append("_");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public static String getColumnType(Class<?> type) {
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return "INTEGER";
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return "BIGINT";
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return "DOUBLE PRECISION";
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return "REAL";
        } else if (type.equals(Short.class) || type.equals(short.class) || type.equals(Byte.class) || type.equals(byte.class)) {
            return "SMALLINT";
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return "CHAR(1)";
        } else if (type.equals(String.class)) {
            return "TEXT";
        } else if (type.equals(LocalDateTime.class)) {
            return "TIMESTAMP WITHOUT TIME ZONE";
        } else if (type.equals(LocalDate.class)) {
            return "DATE";
        } else if (type.equals(LocalTime.class)) {
            return "TIME WITHOUT TIME ZONE";
        } else if (type.equals(UUID.class)) {
            return "UUID";
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }
}
