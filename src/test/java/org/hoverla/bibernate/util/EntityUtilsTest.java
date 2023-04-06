package org.hoverla.bibernate.util;

import org.hoverla.bibernate.exception.session.FieldNotFoundException;
import org.hoverla.bibernate.fixtures.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

class EntityUtilsTest {

    @Test
    void testGetId() {
        Person entity = new Person();
        entity.setId(1);

        Assertions.assertEquals(1, EntityUtils.getId(entity));
    }

    @Test
    void testMapToSnapshot() {
        Person entity = new Person();
        entity.setId(1);
        entity.setName("John");
        entity.setAge(30);

        Object[] snapshot = EntityUtils.mapToSnapshot(entity);

        Assertions.assertArrayEquals(new Object[]{1, "John", 30}, snapshot);
    }

    @Test
    void testGetIdField() {
        Field idField = EntityUtils.getIdField(Person.class);
        Assertions.assertEquals("id", idField.getName());
    }

    @Test
    void testIsIdField() throws NoSuchFieldException {
        Field idField = Person.class.getDeclaredField("id");
        Field nameField = Person.class.getDeclaredField("name");

        Assertions.assertTrue(EntityUtils.isIdField(idField));
        Assertions.assertFalse(EntityUtils.isIdField(nameField));
    }

    @Test
    void testIsColumnField() throws NoSuchFieldException {
        Field idField = Person.class.getDeclaredField("id");
        Field nameField = Person.class.getDeclaredField("name");

        Assertions.assertFalse(EntityUtils.isColumnField(idField));
        Assertions.assertTrue(EntityUtils.isColumnField(nameField));
    }

    @Test
    void testGetFields() {
        Field[] fields = EntityUtils.getFields(Person.class, EntityUtils::isColumnField);
        Assertions.assertEquals(2, fields.length);
        Assertions.assertEquals("name", fields[0].getName());
        Assertions.assertEquals("age", fields[1].getName());
    }

    @Test
    void testGetFieldsForInsert() {
        Field[] fields = EntityUtils.getFieldsForInsert(Person.class);
        Assertions.assertEquals(2, fields.length);
        Assertions.assertEquals("name", fields[0].getName());
        Assertions.assertEquals("age", fields[1].getName());
    }

    @Test
    void testGetFieldsForUpdate() {
        Field[] fields = EntityUtils.getFieldsForUpdate(Person.class);
        Assertions.assertEquals(2, fields.length);
        Assertions.assertEquals("name", fields[0].getName());
        Assertions.assertEquals("age", fields[1].getName());
    }

    @Test
    void testResolveTableName() {
        String tableName = EntityUtils.resolveTableName(Person.class);
        Assertions.assertEquals("person", tableName);
    }

    @Test
    void testResolveColumnName() throws NoSuchFieldException {
        Field nameField = Person.class.getDeclaredField("name");
        Field ageField = Person.class.getDeclaredField("age");

        Assertions.assertEquals("name", EntityUtils.resolveColumnName(nameField));
        Assertions.assertEquals("age", EntityUtils.resolveColumnName(ageField));
    }

    @Test
    void testResolveColumnValue() {
        Timestamp timestamp = Timestamp.valueOf("2022-01-01 00:00:00");
        Date date = Date.valueOf("2022-01-01");

        Object timestampValue = EntityUtils.resolveColumnValue(timestamp);
        Object dateValue = EntityUtils.resolveColumnValue(date);
        Object otherValue = EntityUtils.resolveColumnValue("test");

        Assertions.assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0), timestampValue);
        Assertions.assertEquals(LocalDate.of(2022, 1, 1), dateValue);
        Assertions.assertEquals("test", otherValue);
    }

    @Test
    void testResolveFieldByName() {
        Field field = EntityUtils.resolveFieldByName(Person.class, "name");
        Assertions.assertEquals("name", field.getName());
    }

    @Test
    void testResolveFieldByNameFieldNotFound() {
        Assertions.assertThrows(FieldNotFoundException.class,
            () -> EntityUtils.resolveFieldByName(Person.class, "trash"));
    }
}
