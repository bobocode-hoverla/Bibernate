package org.hoverla.bibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation tells Bibernate that the class is a persistent entity and should be mapped to a database table.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {
}