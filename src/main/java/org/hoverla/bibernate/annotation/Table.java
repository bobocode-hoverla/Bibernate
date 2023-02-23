package org.hoverla.bibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to specify the mapping between an entity class and a database table.
 * It is used to customize the table name, schema, and other properties related to the table.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

    /**
     * Allows to set a table name for a given entity.
     *
     * @return table name
     */
    String name();

    /**
     * Allows to set database schema name.
     *
     * @return schema name
     */
    String schema() default "";

}
