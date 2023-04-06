package org.hoverla.bibernate.annotation.relations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    Class<?> targetEntity();
    String mappedBy();
}
