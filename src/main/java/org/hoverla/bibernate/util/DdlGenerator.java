package org.hoverla.bibernate.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Statement;

@Data
@RequiredArgsConstructor
public class DdlGenerator {
    private final DataSource dataSource;
    private static final String MIGRATION_SQL_FILE = "src/main/resources/migration.sql";

    @SneakyThrows
    public void generateDdl() {
        try(Statement statement = dataSource.getConnection().createStatement()) {
            // Read the contents of the .sql file into a String
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(MIGRATION_SQL_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line);
                    sqlBuilder.append("\n");
                }
            }
            // Execute the SQL statements in the .sql file
            String sql = sqlBuilder.toString();
            statement.execute(sql);
        }
    }
}
