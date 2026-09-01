package com.agenda.service;

import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;

import java.sql.SQLException;
import java.util.List;

public interface AgendaUseCases {
    List<Persona> listarPersonas() throws SQLException;

    int crearPersona(String nombre, String telefonoInicial) throws SQLException;

    void actualizarPersona(int personaId, String nombre) throws SQLException;

    void eliminarPersona(int personaId) throws SQLException;

    List<Telefono> listarTelefonos(int personaId) throws SQLException;

    void agregarTelefono(int personaId, String telefono) throws SQLException;

    void actualizarTelefono(int telefonoId, String telefono) throws SQLException;

    void eliminarTelefono(int telefonoId) throws SQLException;

    List<Direccion> listarDireccionesDePersona(int personaId) throws SQLException;

    List<Direccion> listarDireccionesNoAsociadas(int personaId) throws SQLException;

    int crearYAsociarDireccion(int personaId, String direccion) throws SQLException;

    void asociarDireccion(int personaId, int direccionId) throws SQLException;

    void actualizarDireccion(int direccionId, String direccion) throws SQLException;

    void desasociarDireccion(int personaId, int direccionId) throws SQLException;
}
