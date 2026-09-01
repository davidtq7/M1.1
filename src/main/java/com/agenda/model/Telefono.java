package com.agenda.model;

public class Telefono {
    private final int id;
    private final int personaId;
    private final String telefono;

    public Telefono(int id, int personaId, String telefono) {
        this.id = id;
        this.personaId = personaId;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public int getPersonaId() {
        return personaId;
    }

    public String getTelefono() {
        return telefono;
    }
}
