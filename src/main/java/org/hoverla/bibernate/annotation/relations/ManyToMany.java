package org.hoverla.bibernate.annotation.relationships;

public @interface ManyToMany {
    String mappedBy() default "";
}