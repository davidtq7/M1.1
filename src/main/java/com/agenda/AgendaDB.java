package com.agenda;

import com.agenda.db.AgendaDAO;
import com.agenda.db.DatabaseConfig;
import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;

public final class AgendaDB {
    private AgendaDB() {
    }

    public static void main(String[] args) {
        AgendaDAO dao = new AgendaDAO(DatabaseConfig::connect);
        try {
            for (Persona persona : dao.listarPersonas()) {
                System.out.println(persona.getId() + " - " + persona.getNombre());
                for (Telefono telefono : dao.listarTelefonos(persona.getId())) {
                    System.out.println("  Teléfono: " + telefono.getTelefono());
                }
                for (Direccion direccion : dao.listarDireccionesDePersona(persona.getId())) {
                    System.out.println("  Dirección: " + direccion.getDireccion());
                }
            }
        } catch (Exception exception) {
            System.err.println("No fue posible consultar la agenda: " + exception.getMessage());
        }
    }
}
