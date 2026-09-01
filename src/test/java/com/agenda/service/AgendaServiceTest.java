package com.agenda.service;

import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;
import com.agenda.repository.DireccionRepository;
import com.agenda.repository.PersonaRepository;
import com.agenda.repository.TelefonoRepository;
import com.agenda.validation.DefaultAgendaValidator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgendaServiceTest {
    @Test
    void validaLosDatosAntesDeEnviarlosAlRepositorio() throws Exception {
        PersonaRepositoryDePrueba personas = new PersonaRepositoryDePrueba();
        TelefonoRepositoryDePrueba telefonos = new TelefonoRepositoryDePrueba();
        AgendaService service = new AgendaService(
                personas, telefonos, new DireccionRepositoryDePrueba(), new DefaultAgendaValidator());

        int id = service.crearPersona("  Ana  ", "  555-1234567  ");

        assertEquals(1, id);
        assertEquals("Ana", personas.ultimoNombre);
        assertEquals("555-1234567", personas.ultimoTelefono);
    }

    @Test
    void unTelefonoInvalidoNoLlegaAlRepositorio() {
        TelefonoRepositoryDePrueba telefonos = new TelefonoRepositoryDePrueba();
        AgendaService service = new AgendaService(
                new PersonaRepositoryDePrueba(), telefonos,
                new DireccionRepositoryDePrueba(), new DefaultAgendaValidator());

        assertThrows(IllegalArgumentException.class,
                () -> service.agregarTelefono(1, "ABC"));
        assertEquals(0, telefonos.telefonosAgregados);
    }

    private static class PersonaRepositoryDePrueba implements PersonaRepository {
        private String ultimoNombre;
        private String ultimoTelefono;

        @Override
        public List<Persona> listar() {
            return new ArrayList<>();
        }

        @Override
        public int crear(String nombre, String telefonoInicial) {
            ultimoNombre = nombre;
            ultimoTelefono = telefonoInicial;
            return 1;
        }

        @Override
        public void actualizar(int personaId, String nombre) {
        }

        @Override
        public void eliminar(int id) {
        }
    }

    private static class TelefonoRepositoryDePrueba implements TelefonoRepository {
        private int telefonosAgregados;

        @Override
        public List<Telefono> listarPorPersona(int personaId) {
            return new ArrayList<>();
        }

        @Override
        public void agregar(int personaId, String telefono) {
            telefonosAgregados++;
        }

        @Override
        public void actualizar(int telefonoId, String telefono) {
        }

        @Override
        public void eliminar(int telefonoId) {
        }
    }

    private static class DireccionRepositoryDePrueba implements DireccionRepository {
        @Override
        public List<Direccion> listarPorPersona(int personaId) {
            return new ArrayList<>();
        }

        @Override
        public List<Direccion> listarNoAsociadas(int personaId) {
            return new ArrayList<>();
        }

        @Override
        public int crearYAsociar(int personaId, String direccion) {
            return 1;
        }

        @Override
        public void asociar(int personaId, int direccionId) {
        }

        @Override
        public void actualizar(int direccionId, String direccion) {
        }

        @Override
        public void desasociar(int personaId, int direccionId) {
        }
    }
}
