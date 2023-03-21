package org.hoverla.bibernate.exception.session.jdbc;

public class PrepareStatementFailureException extends RuntimeException {
    private static final String MESSAGE = "Could not prepare statement with SQL: `%s`";

    public PrepareStatementFailureException(String query, Throwable cause) {
        super(MESSAGE.formatted(query), cause);
    }
}
