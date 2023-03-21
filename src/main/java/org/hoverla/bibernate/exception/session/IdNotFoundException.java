package org.hoverla.bibernate.exception.session;

public class IdNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Can't find an id field in class %s";

    public IdNotFoundException(String className) {
        super(MESSAGE.formatted(className));
    }
}
