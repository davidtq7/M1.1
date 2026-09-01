package com.agenda.repository;

import com.agenda.model.Direccion;

import java.sql.SQLException;
import java.util.List;

public interface DireccionRepository {
    List<Direccion> listarPorPersona(int personaId) throws SQLException;

    List<Direccion> listarNoAsociadas(int personaId) throws SQLException;

    int crearYAsociar(int personaId, String direccion) throws SQLException;

    void asociar(int personaId, int direccionId) throws SQLException;

    void actualizar(int direccionId, String direccion) throws SQLException;

    void desasociar(int personaId, int direccionId) throws SQLException;
}
