package org.hoverla.bibernate.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.annotation.*;
import org.hoverla.bibernate.exception.session.FieldNotFoundException;
import org.hoverla.bibernate.exception.session.IdNotFoundException;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class contains lots of helper methods to fetch the
 * column name, its value, entity id, etc.
 */
@Slf4j
@UtilityClass
public class EntityUtils {

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    public static Object getId(Object entity) {
        var entityType = entity.getClass();
        var idField = getIdField(entityType);
        idField.setAccessible(true);
        return idField.get(entity);
    }

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    public static <T> Object[] mapToSnapshot(T entity) {
        log.trace("Creating a snapshot copy of entity {}", entity);
        var fields = entity.getClass().getDeclaredFields();
        var snapshotCopies = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (isColumnField(field) || isIdField(field)) {
                snapshotCopies[i] = field.get(entity);
            }
            //TODO add support for entities, collections ( 1-to-1, 1-to-many relations )
        }
        log.trace("Created a snapshot copy {}", Arrays.toString(snapshotCopies));
        return snapshotCopies;
    }

    public static <T> Field getIdField(Class<T> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
            .filter(EntityUtils::isIdField)
            .findAny()
            .orElseThrow(() -> new IdNotFoundException(entityType.getSimpleName()));
    }

    public static <T> Field getRelatedEntityField(Class<T> fromEntity, Class<?> toEntity) {
        return Arrays.stream(toEntity.getDeclaredFields())
                .filter(field -> field.getType().equals(fromEntity))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find related field"));
    }

    public static boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    public static boolean isColumnField(Field field) {
        return field.isAnnotationPresent(Column.class);
    }

    public static Field[] getFields(Class<?> entityType, Predicate<Field> fieldPredicate) {
        return Arrays.stream(entityType.getDeclaredFields())
            .filter(fieldPredicate)
            .toArray(Field[]::new);
    }

    public static Field[] getFieldsForUpdate(Class<?> entityType) {
        Predicate<Field> updatableFieldPredicate = f -> isColumnField(f) && !isIdField(f);
        return getFields(entityType, updatableFieldPredicate);
    }

    public static Field resolveFieldByName(Class<?> entityType, String fieldName) {
        return Arrays.stream(entityType.getDeclaredFields())
            .filter(field -> field.getName().equalsIgnoreCase(fieldName))
            .findFirst()
            .orElseThrow(() -> new FieldNotFoundException(fieldName, entityType.getSimpleName()));
    }

    public static Field[] getFieldsForInsert(Class<?> entityType) {
        return getFields(entityType, EntityUtils::isColumnField);
    }

    public static <T> String resolveTableName(Class<T> entityType) {
        return Optional.ofNullable(entityType.getAnnotation(Table.class))
            .map(Table::name)
            .orElse(entityType.getSimpleName());
    }

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
            .map(Column::name)
            .orElseGet(field::getName);
    }

    public static Object resolveColumnValue(Object resultSetValue) {
        if (resultSetValue instanceof Timestamp t) {
            return t.toLocalDateTime();
        } else if (resultSetValue instanceof Date d) {
            return d.toLocalDate();
        } else {
            return resultSetValue;
        }
    }

    public static boolean isRegularField(Field field) {
        return isColumnField(field)
                &&!isSingleObjectField(field)
                && !isMultipleObjectField(field);
    }

    public static boolean isSingleObjectField(Field field) {
        return Stream.of(ManyToOne.class, OneToOne.class)
                .anyMatch(field::isAnnotationPresent);
    }

    public static boolean isMultipleObjectField(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }
}
