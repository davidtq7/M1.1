# Agenda Meta 1.2

Programa desarrollado en Java y JavaFX para administrar personas, teléfonos y direcciones utilizando MariaDB.

Una persona puede tener varios teléfonos y varias direcciones. Una misma dirección también puede estar asociada con diferentes personas.

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
