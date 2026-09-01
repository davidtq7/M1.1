package com.agenda.validation;

public interface AgendaValidator {
    String validarNombre(String nombre);

    String validarTelefono(String telefono);

    String validarDireccion(String direccion);
}
