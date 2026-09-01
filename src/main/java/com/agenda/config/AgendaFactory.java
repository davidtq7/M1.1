package com.agenda.config;

import com.agenda.db.ConnectionProvider;
import com.agenda.db.DatabaseConfig;
import com.agenda.repository.DireccionRepository;
import com.agenda.repository.PersonaRepository;
import com.agenda.repository.TelefonoRepository;
import com.agenda.repository.jdbc.JdbcDireccionRepository;
import com.agenda.repository.jdbc.JdbcPersonaRepository;
import com.agenda.repository.jdbc.JdbcTelefonoRepository;
import com.agenda.service.AgendaService;
import com.agenda.service.AgendaUseCases;
import com.agenda.validation.DefaultAgendaValidator;

public final class AgendaFactory {
    private AgendaFactory() {
    }

    public static AgendaUseCases crearAgenda() {
        ConnectionProvider connectionProvider = DatabaseConfig::connect;
        PersonaRepository personas = new JdbcPersonaRepository(connectionProvider);
        TelefonoRepository telefonos = new JdbcTelefonoRepository(connectionProvider);
        DireccionRepository direcciones = new JdbcDireccionRepository(connectionProvider);
        return new AgendaService(personas, telefonos, direcciones, new DefaultAgendaValidator());
    }
}
