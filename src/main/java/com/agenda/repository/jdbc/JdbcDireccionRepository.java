package com.agenda.repository.jdbc;

import com.agenda.db.ConnectionProvider;
import com.agenda.model.Direccion;
import com.agenda.repository.DireccionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcDireccionRepository extends JdbcRepositorySupport implements DireccionRepository {
    public JdbcDireccionRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public List<Direccion> listarPorPersona(int personaId) throws SQLException {
        String sql = "SELECT d.id, d.direccion FROM Direcciones d "
                + "INNER JOIN PersonaDireccion pd ON pd.direccionId = d.id "
                + "WHERE pd.personaId = ? ORDER BY d.direccion, d.id";
        return consultarDirecciones(sql, personaId);
    }

    @Override
    public List<Direccion> listarNoAsociadas(int personaId) throws SQLException {
        String sql = "SELECT d.id, d.direccion FROM Direcciones d "
                + "WHERE NOT EXISTS (SELECT 1 FROM PersonaDireccion pd "
                + "WHERE pd.personaId = ? AND pd.direccionId = d.id) "
                + "ORDER BY d.direccion, d.id";
        return consultarDirecciones(sql, personaId);
    }

    private List<Direccion> consultarDirecciones(String sql, int personaId) throws SQLException {
        List<Direccion> direcciones = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personaId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    direcciones.add(new Direccion(result.getInt("id"), result.getString("direccion")));
                }
            }
        }
        return direcciones;
    }

    @Override
    public int crearYAsociar(int personaId, String direccion) throws SQLException {
        String insertarDireccion = "INSERT INTO Direcciones (direccion) VALUES (?)";
        String asociar = "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)";
        try (Connection connection = connectionProvider.getConnection()) {
            boolean autoCommitAnterior = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int direccionId = insertarDireccion(connection, insertarDireccion, direccion);
                try (PreparedStatement statement = connection.prepareStatement(asociar)) {
                    statement.setInt(1, personaId);
                    statement.setInt(2, direccionId);
                    statement.executeUpdate();
                }
                connection.commit();
                return direccionId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommitAnterior);
            }
        }
    }

    private int insertarDireccion(Connection connection, String sql, String direccion) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, direccion);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No se pudo obtener el id de la dirección.");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void asociar(int personaId, int direccionId) throws SQLException {
        ejecutarActualizacion(
                "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)", statement -> {
                    statement.setInt(1, personaId);
                    statement.setInt(2, direccionId);
                });
    }

    @Override
    public void actualizar(int direccionId, String direccion) throws SQLException {
        ejecutarActualizacion("UPDATE Direcciones SET direccion = ? WHERE id = ?", statement -> {
            statement.setString(1, direccion);
            statement.setInt(2, direccionId);
        });
    }

    @Override
    public void desasociar(int personaId, int direccionId) throws SQLException {
        ejecutarActualizacion(
                "DELETE FROM PersonaDireccion WHERE personaId = ? AND direccionId = ?", statement -> {
                    statement.setInt(1, personaId);
                    statement.setInt(2, direccionId);
                });
    }
}
