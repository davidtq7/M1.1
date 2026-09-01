package com.agenda.repository.jdbc;

import com.agenda.db.ConnectionProvider;
import com.agenda.model.Telefono;
import com.agenda.repository.TelefonoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTelefonoRepository extends JdbcRepositorySupport implements TelefonoRepository {
    public JdbcTelefonoRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public List<Telefono> listarPorPersona(int personaId) throws SQLException {
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

    @Override
    public void agregar(int personaId, String telefono) throws SQLException {
        ejecutarActualizacion("INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)", statement -> {
            statement.setInt(1, personaId);
            statement.setString(2, telefono);
        });
    }

    @Override
    public void actualizar(int telefonoId, String telefono) throws SQLException {
        ejecutarActualizacion("UPDATE Telefonos SET telefono = ? WHERE id = ?", statement -> {
            statement.setString(1, telefono);
            statement.setInt(2, telefonoId);
        });
    }

    @Override
    public void eliminar(int telefonoId) throws SQLException {
        ejecutarActualizacion("DELETE FROM Telefonos WHERE id = ?", statement -> statement.setInt(1, telefonoId));
    }
}
