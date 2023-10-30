package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CustomConnector {
    //
    private final Properties connectionProperties;

    public CustomConnector(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;

        try {
            Class.forName(connectionProperties.getProperty("postgres.driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("PostgreSQL JDBC driver not found. Make sure it's in your classpath.");
        }
    }

    public Connection getConnection() throws SQLException {
        String url = connectionProperties.getProperty("postgres.url");
        String user = connectionProperties.getProperty("postgres.name");
        String password = connectionProperties.getProperty("postgres.password");

        return DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    public Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
