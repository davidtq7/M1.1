package com.agenda.repository;

import com.agenda.model.Persona;

import java.sql.SQLException;
import java.util.List;

public interface PersonaRepository {
    List<Persona> listar() throws SQLException;

    int crear(String nombre, String telefonoInicial) throws SQLException;

    void actualizar(int personaId, String nombre) throws SQLException;

    void eliminar(int personaId) throws SQLException;
}
