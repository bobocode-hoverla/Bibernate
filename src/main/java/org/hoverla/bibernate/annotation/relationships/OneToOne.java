package org.hoverla.bibernate.annotation.relationships;

public @interface OneToOne {
    String mappedBy() default "";
}
