package org.hoverla.bibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to specify the primary key of an entity.
 * It is used to identify a particular entity uniquely in a database table.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Id {
}
