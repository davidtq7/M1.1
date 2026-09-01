package com.agenda.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String DEFAULT_USER = "usuario1";
    private static final String DEFAULT_PASSWORD = "superpassword";

    private DatabaseConfig() {
    }

    public static Connection connect() throws SQLException {
        String url = environmentOrDefault("AGENDA_DB_URL", DEFAULT_URL);
        String user = environmentOrDefault("AGENDA_DB_USER", DEFAULT_USER);
        String password = environmentOrDefault("AGENDA_DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
