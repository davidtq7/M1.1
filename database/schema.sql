CREATE DATABASE IF NOT EXISTS agenda
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agenda;

CREATE TABLE IF NOT EXISTS Personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Telefonos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    personaId INT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    CONSTRAINT fk_telefonos_persona
        FOREIGN KEY (personaId) REFERENCES Personas(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Direcciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(200) NOT NULL
);

-- Tabla puente: permite varias direcciones por persona y compartir una dirección.
CREATE TABLE IF NOT EXISTS PersonaDireccion (
    personaId INT NOT NULL,
    direccionId INT NOT NULL,
    PRIMARY KEY (personaId, direccionId),
    CONSTRAINT fk_persona_direccion_persona
        FOREIGN KEY (personaId) REFERENCES Personas(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_persona_direccion_direccion
        FOREIGN KEY (direccionId) REFERENCES Direcciones(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_telefonos_persona ON Telefonos(personaId);
CREATE INDEX IF NOT EXISTS idx_persona_direccion_direccion ON PersonaDireccion(direccionId);
