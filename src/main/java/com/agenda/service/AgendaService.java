package com.agenda.service;

import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;
import com.agenda.repository.DireccionRepository;
import com.agenda.repository.PersonaRepository;
import com.agenda.repository.TelefonoRepository;
import com.agenda.validation.AgendaValidator;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AgendaService implements AgendaUseCases {
    private final PersonaRepository personas;
    private final TelefonoRepository telefonos;
    private final DireccionRepository direcciones;
    private final AgendaValidator validator;

    public AgendaService(PersonaRepository personas, TelefonoRepository telefonos,
                         DireccionRepository direcciones, AgendaValidator validator) {
        this.personas = Objects.requireNonNull(personas);
        this.telefonos = Objects.requireNonNull(telefonos);
        this.direcciones = Objects.requireNonNull(direcciones);
        this.validator = Objects.requireNonNull(validator);
    }

    @Override
    public List<Persona> listarPersonas() throws SQLException {
        return personas.listar();
    }

    @Override
    public int crearPersona(String nombre, String telefonoInicial) throws SQLException {
        String nombreValido = validator.validarNombre(nombre);
        String telefonoValido = telefonoInicial == null || telefonoInicial.isBlank()
                ? null : validator.validarTelefono(telefonoInicial);
        return personas.crear(nombreValido, telefonoValido);
    }

    @Override
    public void actualizarPersona(int personaId, String nombre) throws SQLException {
        personas.actualizar(personaId, validator.validarNombre(nombre));
    }

    @Override
    public void eliminarPersona(int personaId) throws SQLException {
        personas.eliminar(personaId);
    }

    @Override
    public List<Telefono> listarTelefonos(int personaId) throws SQLException {
        return telefonos.listarPorPersona(personaId);
    }

    @Override
    public void agregarTelefono(int personaId, String telefono) throws SQLException {
        telefonos.agregar(personaId, validator.validarTelefono(telefono));
    }

    @Override
    public void actualizarTelefono(int telefonoId, String telefono) throws SQLException {
        telefonos.actualizar(telefonoId, validator.validarTelefono(telefono));
    }

    @Override
    public void eliminarTelefono(int telefonoId) throws SQLException {
        telefonos.eliminar(telefonoId);
    }

    @Override
    public List<Direccion> listarDireccionesDePersona(int personaId) throws SQLException {
        return direcciones.listarPorPersona(personaId);
    }

    @Override
    public List<Direccion> listarDireccionesNoAsociadas(int personaId) throws SQLException {
        return direcciones.listarNoAsociadas(personaId);
    }

    @Override
    public int crearYAsociarDireccion(int personaId, String direccion) throws SQLException {
        return direcciones.crearYAsociar(personaId, validator.validarDireccion(direccion));
    }

    @Override
    public void asociarDireccion(int personaId, int direccionId) throws SQLException {
        direcciones.asociar(personaId, direccionId);
    }

    @Override
    public void actualizarDireccion(int direccionId, String direccion) throws SQLException {
        direcciones.actualizar(direccionId, validator.validarDireccion(direccion));
    }

    @Override
    public void desasociarDireccion(int personaId, int direccionId) throws SQLException {
        direcciones.desasociar(personaId, direccionId);
    }
}
