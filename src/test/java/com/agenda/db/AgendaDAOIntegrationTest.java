package com.agenda.db;

import com.agenda.model.Direccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendaDAOIntegrationTest {
    private static final String URL = "jdbc:h2:mem:agenda;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private AgendaDAO dao;

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
        dao = new AgendaDAO(() -> DriverManager.getConnection(URL, "sa", ""));
    }

    @Test
    void realizaCrudDePersonaYVariosTelefonos() throws Exception {
        int personaId = dao.crearPersona("Ana", "555-1111111");
        dao.agregarTelefono(personaId, "555-2222222");

        assertEquals(1, dao.listarPersonas().size());
        assertEquals(2, dao.listarTelefonos(personaId).size());

        int telefonoId = dao.listarTelefonos(personaId).get(0).getId();
        dao.actualizarTelefono(telefonoId, "555-3333333");
        assertTrue(dao.listarTelefonos(personaId).stream()
                .anyMatch(telefono -> telefono.getTelefono().equals("555-3333333")));

        dao.eliminarPersona(personaId);
        assertTrue(dao.listarPersonas().isEmpty());
        assertTrue(dao.listarTelefonos(personaId).isEmpty());
    }

    @Test
    void permiteVariasDireccionesYUnaDireccionCompartida() throws Exception {
        int anaId = dao.crearPersona("Ana", null);
        int luisId = dao.crearPersona("Luis", null);

        int compartidaId = dao.crearYAsociarDireccion(anaId, "Calle Uno 10");
        dao.crearYAsociarDireccion(anaId, "Calle Dos 20");
        dao.asociarDireccion(luisId, compartidaId);

        assertEquals(2, dao.listarDireccionesDePersona(anaId).size());
        assertEquals(1, dao.listarDireccionesDePersona(luisId).size());

        dao.actualizarDireccion(compartidaId, "Calle Uno 99");
        Direccion direccionDeLuis = dao.listarDireccionesDePersona(luisId).get(0);
        assertEquals("Calle Uno 99", direccionDeLuis.getDireccion());

        dao.desasociarDireccion(anaId, compartidaId);
        assertEquals(1, dao.listarDireccionesDePersona(anaId).size());
        assertEquals(1, dao.listarDireccionesDePersona(luisId).size());
    }
}
