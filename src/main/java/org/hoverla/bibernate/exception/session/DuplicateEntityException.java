package org.hoverla.bibernate.exception.session;

public class DuplicateEntityException extends RuntimeException {
    private static final String MESSAGE = "Entity %s is already present in persistence context";

    public DuplicateEntityException(Object entity) {
        super(MESSAGE.formatted(entity));
    }
}
