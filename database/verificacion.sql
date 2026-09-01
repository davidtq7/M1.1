USE agenda;

-- Una fila por teléfono: permite comprobar que no se pierde el segundo número.
SELECT p.id, p.nombre, t.id AS telefonoId, t.telefono
FROM Personas p
LEFT JOIN Telefonos t ON t.personaId = p.id
ORDER BY p.id, t.id;

-- Una fila por relación persona-dirección: una dirección compartida aparecerá
-- con el mismo direccionId para dos o más personas.
SELECT p.id AS personaId, p.nombre, d.id AS direccionId, d.direccion
FROM Personas p
INNER JOIN PersonaDireccion pd ON pd.personaId = p.id
INNER JOIN Direcciones d ON d.id = pd.direccionId
ORDER BY d.id, p.id;
