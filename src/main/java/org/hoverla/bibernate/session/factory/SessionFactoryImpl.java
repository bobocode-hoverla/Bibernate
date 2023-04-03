package org.hoverla.bibernate.session.factory;

import lombok.SneakyThrows;
import org.hoverla.bibernate.annotation.Column;
import org.hoverla.bibernate.annotation.Entity;
import org.hoverla.bibernate.annotation.Table;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.session.DefaultSession;
import org.hoverla.bibernate.session.Session;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;

    public SessionFactoryImpl(DataSource dataSource, Configuration configuration) {
        checkAutoDdlCreation(configuration);
        this.dataSource = dataSource;
    }

    @Override
    public Session openSession() {
        return new DefaultSession(dataSource);
    }

    @Override
    public void close() {
    }

    @SneakyThrows
    private void checkAutoDdlCreation(Configuration configuration) {
        var isAutoDdl = configuration.isAutoDdlCreation();
        String entitiesPackage = configuration.getEntitiesPackage();
        Reflections reflections = new Reflections(entitiesPackage);

        var entities = reflections.getTypesAnnotatedWith(Entity.class);

        if (isAutoDdl) {
            var tableNames = entities.stream()
                    .map(this::resolveEntityName)
                    .toList();

            var connection = dataSource.getConnection();
            var preparedStatement = connection.prepareStatement("DROP TABLE ? ");

            dropTables(tableNames, preparedStatement);

            //todo create tables
            initTables(tableNames, preparedStatement);
        }
    }

    private void initTables(List<Class<?>> entities, PreparedStatement preparedStatement) {
        for (Class<?> entity : entities) {
            Map<String, Class<?>> resolvedEntityFields = new HashMap<>();
            var resolvedName = resolveEntityName(entity);
            Field[] entityFields = entity.getDeclaredFields();

            for (Field entityField : entityFields) {
                resolvedEntityFields.put(resolveFieldName(entityField), entityField.getType());
            }

            String sqlToInsert = """
                    CREATE TABLE ? (
                       ? ?
                    );
                    """;

        }
    }

    private String resolveEntityName(Class<?> entity) {
        return ofNullable(entity.getAnnotation(Table.class))
                .map(Table::name)
                .filter(String::isBlank)
                .orElse(entity.getSimpleName().toLowerCase());
    }

    private String resolveFieldName(Field field) {
        var entityClass = field.getClass();
        Optional<Column> entityColumn = ofNullable(entityClass.getAnnotation(Column.class));
        return entityColumn
                .map(Column::name)
                .orElse(entityClass.getSimpleName());
    }

    private void dropTables(List<String> tableNames, PreparedStatement preparedStatement) {

        tableNames.forEach(tableName -> {
            try {
                preparedStatement.setString(1, tableName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}