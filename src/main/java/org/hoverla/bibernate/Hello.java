package org.hoverla.bibernate;

import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.configuration.PropertiesConfiguration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Hello {
    public static void main(String[] args) throws SQLException {
        Configuration configuration = new PropertiesConfiguration();
        DataSource bibariDataSource = new BibariDataSource(configuration);
        Connection connection = bibariDataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * from persons;");
        resultSet.next();
        String username = resultSet.getString("username");
        System.out.println(username);
    }
}
