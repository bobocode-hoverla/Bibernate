package org.hoverla.bibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to set one-to-many relation between entities.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface OneToMany {
}
