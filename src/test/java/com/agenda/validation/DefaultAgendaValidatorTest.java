package com.agenda.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultAgendaValidatorTest {
    private final AgendaValidator validator = new DefaultAgendaValidator();

    @Test
    void limpiaEspaciosDelNombre() {
        assertEquals("Ana López", validator.validarNombre("  Ana López  "));
    }

    @Test
    void rechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> validator.validarNombre("  "));
    }

    @Test
    void aceptaTelefonoComun() {
        assertEquals("+52 (664) 123-4567", validator.validarTelefono("+52 (664) 123-4567"));
    }

    @Test
    void rechazaTelefonoConLetras() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarTelefono("555-ABC-123"));
    }

    @Test
    void rechazaDireccionMayorA200Caracteres() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarDireccion("x".repeat(201)));
    }
}
