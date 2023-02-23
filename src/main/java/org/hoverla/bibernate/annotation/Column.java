package org.hoverla.bibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to specify the name of the column in the database table that is mapped to the entity field.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {

    /**
     * Allows to specify a column name for the field.
     *
     * @return column name
     */
    String name();

}
