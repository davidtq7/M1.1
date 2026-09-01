package com.agenda;

import com.agenda.config.AgendaFactory;
import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;
import com.agenda.service.AgendaUseCases;

public final class AgendaDB {
    private AgendaDB() {
    }

    public static void main(String[] args) {
        AgendaUseCases agenda = AgendaFactory.crearAgenda();
        try {
            for (Persona persona : agenda.listarPersonas()) {
                System.out.println(persona.getId() + " - " + persona.getNombre());
                for (Telefono telefono : agenda.listarTelefonos(persona.getId())) {
                    System.out.println("  Teléfono: " + telefono.getTelefono());
                }
                for (Direccion direccion : agenda.listarDireccionesDePersona(persona.getId())) {
                    System.out.println("  Dirección: " + direccion.getDireccion());
                }
            }
        } catch (Exception exception) {
            System.err.println("No fue posible consultar la agenda: " + exception.getMessage());
        }
    }
}
