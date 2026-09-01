package com.agenda.db;

import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {
    private final ConnectionProvider connectionProvider;

    public AgendaDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<Persona> listarPersonas() throws SQLException {
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

    /** Crea la persona y, si se indicó, su primer teléfono en una sola transacción. */
    public int crearPersona(String nombre, String telefonoInicial) throws SQLException {
        String insertarPersona = "INSERT INTO Personas (nombre) VALUES (?)";
        String insertarTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection connection = connectionProvider.getConnection()) {
            boolean autoCommitAnterior = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int personaId;
                try (PreparedStatement statement = connection.prepareStatement(
                        insertarPersona, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, nombre);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo obtener el id de la persona.");
                        }
                        personaId = keys.getInt(1);
                    }
                }
                if (telefonoInicial != null && !telefonoInicial.isBlank()) {
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

    public void actualizarPersona(int personaId, String nombre) throws SQLException {
        ejecutarActualizacion("UPDATE Personas SET nombre = ? WHERE id = ?", statement -> {
            statement.setString(1, nombre);
            statement.setInt(2, personaId);
        });
    }

    public void eliminarPersona(int personaId) throws SQLException {
        ejecutarActualizacion("DELETE FROM Personas WHERE id = ?", statement -> statement.setInt(1, personaId));
    }

    public List<Telefono> listarTelefonos(int personaId) throws SQLException {
        String sql = "SELECT id, personaId, telefono FROM Telefonos WHERE personaId = ? ORDER BY id";
        List<Telefono> telefonos = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personaId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    telefonos.add(new Telefono(
                            result.getInt("id"), result.getInt("personaId"), result.getString("telefono")));
                }
            }
        }
        return telefonos;
    }

    public void agregarTelefono(int personaId, String telefono) throws SQLException {
        ejecutarActualizacion("INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)", statement -> {
            statement.setInt(1, personaId);
            statement.setString(2, telefono);
        });
    }

    public void actualizarTelefono(int telefonoId, String telefono) throws SQLException {
        ejecutarActualizacion("UPDATE Telefonos SET telefono = ? WHERE id = ?", statement -> {
            statement.setString(1, telefono);
            statement.setInt(2, telefonoId);
        });
    }

    public void eliminarTelefono(int telefonoId) throws SQLException {
        ejecutarActualizacion("DELETE FROM Telefonos WHERE id = ?", statement -> statement.setInt(1, telefonoId));
    }

    public List<Direccion> listarDireccionesDePersona(int personaId) throws SQLException {
        String sql = "SELECT d.id, d.direccion FROM Direcciones d "
                + "INNER JOIN PersonaDireccion pd ON pd.direccionId = d.id "
                + "WHERE pd.personaId = ? ORDER BY d.direccion, d.id";
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

    public List<Direccion> listarDireccionesNoAsociadas(int personaId) throws SQLException {
        String sql = "SELECT d.id, d.direccion FROM Direcciones d "
                + "WHERE NOT EXISTS (SELECT 1 FROM PersonaDireccion pd "
                + "WHERE pd.personaId = ? AND pd.direccionId = d.id) "
                + "ORDER BY d.direccion, d.id";
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

    /** Crea una dirección y la asocia a la persona de forma atómica. */
    public int crearYAsociarDireccion(int personaId, String direccion) throws SQLException {
        String insertarDireccion = "INSERT INTO Direcciones (direccion) VALUES (?)";
        String asociar = "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)";
        try (Connection connection = connectionProvider.getConnection()) {
            boolean autoCommitAnterior = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int direccionId;
                try (PreparedStatement statement = connection.prepareStatement(
                        insertarDireccion, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, direccion);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo obtener el id de la dirección.");
                        }
                        direccionId = keys.getInt(1);
                    }
                }
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

    public void asociarDireccion(int personaId, int direccionId) throws SQLException {
        ejecutarActualizacion(
                "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)", statement -> {
                    statement.setInt(1, personaId);
                    statement.setInt(2, direccionId);
                });
    }

    /** Cambiar una dirección compartida cambia el texto que ven todas las personas asociadas. */
    public void actualizarDireccion(int direccionId, String direccion) throws SQLException {
        ejecutarActualizacion("UPDATE Direcciones SET direccion = ? WHERE id = ?", statement -> {
            statement.setString(1, direccion);
            statement.setInt(2, direccionId);
        });
    }

    /** Quita únicamente la relación; no borra la dirección ni afecta a otras personas. */
    public void desasociarDireccion(int personaId, int direccionId) throws SQLException {
        ejecutarActualizacion(
                "DELETE FROM PersonaDireccion WHERE personaId = ? AND direccionId = ?", statement -> {
                    statement.setInt(1, personaId);
                    statement.setInt(2, direccionId);
                });
    }

    private void ejecutarActualizacion(String sql, StatementBinder binder) throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            statement.executeUpdate();
        }
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
