package com.agenda.validation;

public class DefaultAgendaValidator implements AgendaValidator {
    @Override
    public String validarNombre(String nombre) {
        return textoObligatorio(nombre, "El nombre", 100);
    }

    @Override
    public String validarDireccion(String direccion) {
        return textoObligatorio(direccion, "La dirección", 200);
    }

    @Override
    public String validarTelefono(String telefono) {
        String limpio = textoObligatorio(telefono, "El teléfono", 20);
        if (!limpio.matches("[0-9+()\\-\\s]{7,20}")) {
            throw new IllegalArgumentException(
                    "El teléfono contiene caracteres no válidos o es demasiado corto.");
        }
        return limpio;
    }

    private String textoObligatorio(String valor, String campo, int maximo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        if (limpio.length() > maximo) {
            throw new IllegalArgumentException(campo + " no puede exceder " + maximo + " caracteres.");
        }
        return limpio;
    }
}
