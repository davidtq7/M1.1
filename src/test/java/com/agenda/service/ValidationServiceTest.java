package com.agenda.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationServiceTest {
    @Test
    void limpiaEspaciosDelNombre() {
        assertEquals("Ana López", ValidationService.validarNombre("  Ana López  "));
    }

    @Test
    void rechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> ValidationService.validarNombre("  "));
    }

    @Test
    void aceptaTelefonoComun() {
        assertEquals("+52 (664) 123-4567", ValidationService.validarTelefono("+52 (664) 123-4567"));
    }

    @Test
    void rechazaTelefonoConLetras() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationService.validarTelefono("555-ABC-123"));
    }

    @Test
    void rechazaDireccionMayorA200Caracteres() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationService.validarDireccion("x".repeat(201)));
    }
}
