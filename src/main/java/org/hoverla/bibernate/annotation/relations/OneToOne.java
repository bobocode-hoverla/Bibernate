package org.hoverla.bibernate.annotation.relations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a one-to-one mapping between two entities.
 *
 * <p>
 * This annotation is used to specify a one-to-one association
 * between two entities in a JPA application. It can be
 * applied to a field or a getter method.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {

    /**
     * The name of the inverse side of the relationship.
     *
     * <p>
     * This element specifies the name of the field or property in the target entity that owns the relationship. This
     * attribute is only used when the target entity is the owning side of the relationship.
     * </p>
     * @return the name of the inverse side of the relationship.
     */
    String mappedBy() default "";

    /**
     * The target entity of the association.
     * <p>
     * This element specifies the entity class that is the target of the association. It is required unless the
     * association is unidirectional.
     * </p>
     *
     * @return the target entity of the association.
     */
    Class<?> targetEntity();
}
