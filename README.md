# Agenda Meta 1.3

Programa desarrollado en Java y JavaFX para administrar personas, teléfonos y direcciones utilizando MariaDB.

Una persona puede tener varios teléfonos y varias direcciones. Una misma dirección también puede estar asociada con diferentes personas.

En esta versión se separaron las responsabilidades de la interfaz, las reglas del programa, la validación y el acceso a datos. Se utilizaron interfaces para que el servicio no dependa directamente de las clases JDBC.

## Organización principal

- `App`: interfaz gráfica JavaFX.
- `AgendaService`: coordina las operaciones de la agenda.
- `repository`: contratos de acceso a personas, teléfonos y direcciones.
- `repository.jdbc`: implementación de los repositorios con JDBC.
- `validation`: validación de los datos capturados.
- `config`: creación y conexión de los componentes.

## Requisitos

- JDK 17 o posterior
- MariaDB
- Visual Studio Code

## Base de datos

Los scripts para crear y comprobar la base de datos se encuentran en la carpeta `database`. Para una instalación nueva se utiliza `database/schema.sql`.

La conexión predeterminada utiliza la base `agenda`, el usuario `usuario1` y la contraseña `superpassword`.

## Pruebas y ejecución

Desde una terminal abierta en la carpeta del proyecto:

```powershell
.\mvnw.cmd test
.\mvnw.cmd javafx:run
```

Las pruebas unitarias revisan la validación y el servicio. Las pruebas de integración utilizan una base H2 temporal para comprobar los repositorios JDBC y las relaciones entre personas y direcciones.
