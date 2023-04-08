package org.hoverla.bibernate.util;

import lombok.RequiredArgsConstructor;
import org.hoverla.bibernate.exception.queryexecutor.UnableExecuteSqlQueryException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A utility class for executing SQL queries on a database.
 */
@RequiredArgsConstructor
public class QueryExecutor {

    /**
     * The data source to be used for executing SQL queries.
     */
    private final DataSource dataSource;

    /**
     Executes the specified SQL query on the database.

     @param sql the SQL query to execute.
     @throws UnableExecuteSqlQueryException if the query cannot be executed.
     */
    public void execute(String sql) {
        try(Statement statement = dataSource.getConnection().createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new UnableExecuteSqlQueryException("Cannot execute the next query: " + sql, e);
        }
    }
}
