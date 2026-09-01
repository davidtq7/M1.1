-- Ejecutar una sola vez sobre la base de Meta 1, antes de iniciar la aplicación.
-- Hace respaldo de Personas y conserva todas las direcciones no vacías.
USE agenda;

CREATE TABLE Personas_respaldo_meta1 AS SELECT * FROM Personas;

CREATE TABLE Direcciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(200) NOT NULL
);

CREATE TABLE PersonaDireccion (
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

INSERT INTO Direcciones (direccion)
SELECT DISTINCT TRIM(direccion)
FROM Personas
WHERE direccion IS NOT NULL AND TRIM(direccion) <> '';

INSERT INTO PersonaDireccion (personaId, direccionId)
SELECT p.id, d.id
FROM Personas p
INNER JOIN Direcciones d ON d.direccion = TRIM(p.direccion)
WHERE p.direccion IS NOT NULL AND TRIM(p.direccion) <> '';

ALTER TABLE Personas DROP COLUMN direccion;
CREATE INDEX idx_persona_direccion_direccion ON PersonaDireccion(direccionId);
