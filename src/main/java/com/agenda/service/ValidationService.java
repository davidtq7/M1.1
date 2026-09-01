package com.agenda.service;

public final class ValidationService {
    private ValidationService() {
    }

    public static String validarNombre(String nombre) {
        return textoObligatorio(nombre, "El nombre", 100);
    }

    public static String validarDireccion(String direccion) {
        return textoObligatorio(direccion, "La dirección", 200);
    }

    public static String validarTelefono(String telefono) {
        String limpio = textoObligatorio(telefono, "El teléfono", 20);
        if (!limpio.matches("[0-9+()\\-\\s]{7,20}")) {
            throw new IllegalArgumentException("El teléfono contiene caracteres no válidos o es demasiado corto.");
        }
        return limpio;
    }

    private static String textoObligatorio(String valor, String campo, int maximo) {
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
