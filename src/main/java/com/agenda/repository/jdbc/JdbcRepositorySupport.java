package com.agenda.repository.jdbc;

import com.agenda.db.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

abstract class JdbcRepositorySupport {
    protected final ConnectionProvider connectionProvider;

    protected JdbcRepositorySupport(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider);
    }

    protected void ejecutarActualizacion(String sql, StatementBinder binder) throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            statement.executeUpdate();
        }
    }

    @FunctionalInterface
    protected interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
