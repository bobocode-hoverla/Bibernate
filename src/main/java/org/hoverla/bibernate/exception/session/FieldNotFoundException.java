package org.hoverla.bibernate.exception.session;

public class FieldNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Can't find an `%s` field in class `%s`";

    public FieldNotFoundException(String fieldName, String className) {
        super(MESSAGE.formatted(fieldName, className));
    }
}
