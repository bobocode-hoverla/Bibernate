package org.hoverla.bibernate.util;

import org.hoverla.bibernate.fixtures.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SqlUtilsTest {

    @Test
    void testGetCommaSeparatedInsertableColumns() {
        String result = SqlUtils.getCommaSeparatedInsertableColumns(Person.class);
        Assertions.assertEquals("name, age", result);
    }

    @Test
    void testGetCommaSeparatedInsertableParams() {
        String result = SqlUtils.getCommaSeparatedInsertableParams(Person.class);
        Assertions.assertEquals("?,?", result);
    }

    @Test
    void testGetCommaSeparatedUpdatableColumns() {
        String result = SqlUtils.getCommaSeparatedUpdatableColumns(Person.class);
        Assertions.assertEquals("name = ?, age = ?", result);
    }
}
