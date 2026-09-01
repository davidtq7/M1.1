package com.agenda.repository.jdbc;

import com.agenda.db.ConnectionProvider;
import com.agenda.model.Direccion;
import com.agenda.service.AgendaService;
import com.agenda.service.AgendaUseCases;
import com.agenda.validation.DefaultAgendaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcRepositoriesIntegrationTest {
    private static final String URL = "jdbc:h2:mem:agenda;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private AgendaUseCases agenda;

    @BeforeEach
    void prepararBaseTemporal() throws Exception {
        try (Connection connection = DriverManager.getConnection(URL, "sa", "");
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE Personas ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL)");
            statement.execute("CREATE TABLE Telefonos ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, personaId INT NOT NULL, telefono VARCHAR(20) NOT NULL, "
                    + "FOREIGN KEY (personaId) REFERENCES Personas(id) ON DELETE CASCADE ON UPDATE CASCADE)");
            statement.execute("CREATE TABLE Direcciones ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, direccion VARCHAR(200) NOT NULL)");
            statement.execute("CREATE TABLE PersonaDireccion ("
                    + "personaId INT NOT NULL, direccionId INT NOT NULL, "
                    + "PRIMARY KEY (personaId, direccionId), "
                    + "FOREIGN KEY (personaId) REFERENCES Personas(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (direccionId) REFERENCES Direcciones(id) ON DELETE CASCADE)");
        }

        ConnectionProvider provider = () -> DriverManager.getConnection(URL, "sa", "");
        agenda = new AgendaService(
                new JdbcPersonaRepository(provider),
                new JdbcTelefonoRepository(provider),
                new JdbcDireccionRepository(provider),
                new DefaultAgendaValidator());
    }

    @Test
    void realizaCrudDePersonaYVariosTelefonos() throws Exception {
        int personaId = agenda.crearPersona("Ana", "555-1111111");
        agenda.agregarTelefono(personaId, "555-2222222");

        assertEquals(1, agenda.listarPersonas().size());
        assertEquals(2, agenda.listarTelefonos(personaId).size());

        int telefonoId = agenda.listarTelefonos(personaId).get(0).getId();
        agenda.actualizarTelefono(telefonoId, "555-3333333");
        assertTrue(agenda.listarTelefonos(personaId).stream()
                .anyMatch(telefono -> telefono.getTelefono().equals("555-3333333")));

        agenda.eliminarPersona(personaId);
        assertTrue(agenda.listarPersonas().isEmpty());
        assertTrue(agenda.listarTelefonos(personaId).isEmpty());
    }

    @Test
    void permiteVariasDireccionesYUnaDireccionCompartida() throws Exception {
        int anaId = agenda.crearPersona("Ana", null);
        int luisId = agenda.crearPersona("Luis", null);

        int compartidaId = agenda.crearYAsociarDireccion(anaId, "Calle Uno 10");
        agenda.crearYAsociarDireccion(anaId, "Calle Dos 20");
        agenda.asociarDireccion(luisId, compartidaId);

        assertEquals(2, agenda.listarDireccionesDePersona(anaId).size());
        assertEquals(1, agenda.listarDireccionesDePersona(luisId).size());

        agenda.actualizarDireccion(compartidaId, "Calle Uno 99");
        Direccion direccionDeLuis = agenda.listarDireccionesDePersona(luisId).get(0);
        assertEquals("Calle Uno 99", direccionDeLuis.getDireccion());

        agenda.desasociarDireccion(anaId, compartidaId);
        assertEquals(1, agenda.listarDireccionesDePersona(anaId).size());
        assertEquals(1, agenda.listarDireccionesDePersona(luisId).size());
    }
}
