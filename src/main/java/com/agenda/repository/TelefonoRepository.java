package com.agenda.repository;

import com.agenda.model.Telefono;

import java.sql.SQLException;
import java.util.List;

public interface TelefonoRepository {
    List<Telefono> listarPorPersona(int personaId) throws SQLException;

    void agregar(int personaId, String telefono) throws SQLException;

    void actualizar(int telefonoId, String telefono) throws SQLException;

    void eliminar(int telefonoId) throws SQLException;
}
