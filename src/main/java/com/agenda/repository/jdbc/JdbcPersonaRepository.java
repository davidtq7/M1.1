package com.agenda.repository.jdbc;

import com.agenda.db.ConnectionProvider;
import com.agenda.model.Persona;
import com.agenda.repository.PersonaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcPersonaRepository extends JdbcRepositorySupport implements PersonaRepository {
    public JdbcPersonaRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public List<Persona> listar() throws SQLException {
        String sql = "SELECT id, nombre FROM Personas ORDER BY nombre, id";
        List<Persona> personas = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                personas.add(new Persona(result.getInt("id"), result.getString("nombre")));
            }
        }
        return personas;
    }

    @Override
    public int crear(String nombre, String telefonoInicial) throws SQLException {
        String insertarPersona = "INSERT INTO Personas (nombre) VALUES (?)";
        String insertarTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection connection = connectionProvider.getConnection()) {
            boolean autoCommitAnterior = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int personaId = insertarPersona(connection, insertarPersona, nombre);
                if (telefonoInicial != null) {
                    try (PreparedStatement statement = connection.prepareStatement(insertarTelefono)) {
                        statement.setInt(1, personaId);
                        statement.setString(2, telefonoInicial);
                        statement.executeUpdate();
                    }
                }
                connection.commit();
                return personaId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommitAnterior);
            }
        }
    }

    private int insertarPersona(Connection connection, String sql, String nombre) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nombre);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No se pudo obtener el id de la persona.");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(int personaId, String nombre) throws SQLException {
        ejecutarActualizacion("UPDATE Personas SET nombre = ? WHERE id = ?", statement -> {
            statement.setString(1, nombre);
            statement.setInt(2, personaId);
        });
    }

    @Override
    public void eliminar(int personaId) throws SQLException {
        ejecutarActualizacion("DELETE FROM Personas WHERE id = ?", statement -> statement.setInt(1, personaId));
    }
}
