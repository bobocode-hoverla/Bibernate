package org.hoverla.bibernate.annotation.relationships;

import jakarta.persistence.OneToMany;

public @interface ManyToOne {
    String mappedBy() default "";
}