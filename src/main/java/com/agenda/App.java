package com.agenda;

import com.agenda.db.AgendaDAO;
import com.agenda.db.DatabaseConfig;
import com.agenda.model.Direccion;
import com.agenda.model.Persona;
import com.agenda.model.Telefono;
import com.agenda.service.ValidationService;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class App extends Application {
    private final AgendaDAO dao = new AgendaDAO(DatabaseConfig::connect);

    private final TableView<Persona> tablaPersonas = new TableView<>();
    private final TableView<Telefono> tablaTelefonos = new TableView<>();
    private final TableView<Direccion> tablaDirecciones = new TableView<>();

    private final TextField txtNombre = new TextField();
    private final TextField txtTelefonoInicial = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtDireccion = new TextField();
    private final ComboBox<Direccion> cmbDireccionesExistentes = new ComboBox<>();

    @Override
    public void start(Stage stage) {
        configurarTablas();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setTop(crearFormularioPersona());
        root.setCenter(crearContenido());

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> seleccionarPersona(actual));
        tablaTelefonos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> txtTelefono.setText(actual == null ? "" : actual.getTelefono()));
        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> txtDireccion.setText(actual == null ? "" : actual.getDireccion()));

        ejecutar(() -> recargarPersonas(null));

        stage.setTitle("Agenda - Meta 1.2");
        stage.setScene(new Scene(root, 1050, 650));
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.show();
    }

    private GridPane crearFormularioPersona() {
        txtNombre.setPromptText("Nombre completo");
        txtTelefonoInicial.setPromptText("Opcional al crear");

        Button btnNuevo = new Button("Guardar persona");
        Button btnActualizar = new Button("Actualizar nombre");
        Button btnEliminar = new Button("Eliminar persona");
        Button btnLimpiar = new Button("Limpiar selección");

        btnNuevo.setOnAction(event -> guardarPersona());
        btnActualizar.setOnAction(event -> actualizarPersona());
        btnEliminar.setOnAction(event -> eliminarPersona());
        btnLimpiar.setOnAction(event -> limpiarSeleccion());

        GridPane panel = new GridPane();
        panel.setHgap(10);
        panel.setVgap(8);
        panel.setPadding(new Insets(0, 0, 14, 0));
        panel.add(new Label("Nombre:"), 0, 0);
        panel.add(txtNombre, 1, 0);
        panel.add(new Label("Primer teléfono:"), 2, 0);
        panel.add(txtTelefonoInicial, 3, 0);
        panel.add(new HBox(8, btnNuevo, btnActualizar, btnEliminar, btnLimpiar), 1, 1, 3, 1);
        GridPane.setHgrow(txtNombre, Priority.ALWAYS);
        GridPane.setHgrow(txtTelefonoInicial, Priority.ALWAYS);
        return panel;
    }

    private SplitPane crearContenido() {
        VBox personas = new VBox(8, new Label("Personas"), tablaPersonas);
        VBox.setVgrow(tablaPersonas, Priority.ALWAYS);

        VBox detalles = new VBox(12,
                crearPanelTelefonos(), new Separator(), crearPanelDirecciones());
        VBox.setVgrow(detalles.getChildren().get(0), Priority.ALWAYS);
        VBox.setVgrow(detalles.getChildren().get(2), Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(personas, detalles);
        splitPane.setDividerPositions(0.36);
        return splitPane;
    }

    private VBox crearPanelTelefonos() {
        txtTelefono.setPromptText("Ej. 555-1234567");
        Button btnAgregar = new Button("Agregar");
        Button btnActualizar = new Button("Actualizar seleccionado");
        Button btnEliminar = new Button("Eliminar seleccionado");
        btnAgregar.setOnAction(event -> agregarTelefono());
        btnActualizar.setOnAction(event -> actualizarTelefono());
        btnEliminar.setOnAction(event -> eliminarTelefono());

        HBox controles = new HBox(8, new Label("Número:"), txtTelefono,
                btnAgregar, btnActualizar, btnEliminar);
        controles.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtTelefono, Priority.ALWAYS);
        VBox panel = new VBox(8, new Label("Teléfonos de la persona seleccionada"), controles, tablaTelefonos);
        VBox.setVgrow(tablaTelefonos, Priority.ALWAYS);
        return panel;
    }

    private VBox crearPanelDirecciones() {
        txtDireccion.setPromptText("Calle, número, colonia, ciudad");
        cmbDireccionesExistentes.setPromptText("Elegir una dirección ya registrada");
        cmbDireccionesExistentes.setMaxWidth(Double.MAX_VALUE);

        Button btnCrear = new Button("Crear y asociar");
        Button btnEditar = new Button("Modificar seleccionada");
        Button btnQuitar = new Button("Quitar de esta persona");
        Button btnCompartir = new Button("Asociar existente");
        btnCrear.setOnAction(event -> crearDireccion());
        btnEditar.setOnAction(event -> actualizarDireccion());
        btnQuitar.setOnAction(event -> desasociarDireccion());
        btnCompartir.setOnAction(event -> asociarDireccionExistente());

        HBox nueva = new HBox(8, new Label("Dirección:"), txtDireccion,
                btnCrear, btnEditar, btnQuitar);
        nueva.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtDireccion, Priority.ALWAYS);
        HBox existente = new HBox(8, new Label("Compartir:"), cmbDireccionesExistentes, btnCompartir);
        existente.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cmbDireccionesExistentes, Priority.ALWAYS);

        VBox panel = new VBox(8, new Label("Direcciones de la persona seleccionada"),
                nueva, existente, tablaDirecciones);
        VBox.setVgrow(tablaDirecciones, Priority.ALWAYS);
        return panel;
    }

    private void configurarTablas() {
        TableColumn<Persona, Number> personaId = new TableColumn<>("ID");
        personaId.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getId()));
        TableColumn<Persona, String> personaNombre = new TableColumn<>("Nombre");
        personaNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNombre()));
        tablaPersonas.getColumns().addAll(personaId, personaNombre);
        tablaPersonas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaPersonas.setPlaceholder(new Label("No hay personas registradas"));

        TableColumn<Telefono, Number> telefonoId = new TableColumn<>("ID");
        telefonoId.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getId()));
        TableColumn<Telefono, String> telefonoNumero = new TableColumn<>("Teléfono");
        telefonoNumero.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTelefono()));
        tablaTelefonos.getColumns().addAll(telefonoId, telefonoNumero);
        tablaTelefonos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaTelefonos.setPlaceholder(new Label("Seleccione una persona"));

        TableColumn<Direccion, Number> direccionId = new TableColumn<>("ID");
        direccionId.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getId()));
        TableColumn<Direccion, String> direccionTexto = new TableColumn<>("Dirección");
        direccionTexto.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDireccion()));
        tablaDirecciones.getColumns().addAll(direccionId, direccionTexto);
        tablaDirecciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaDirecciones.setPlaceholder(new Label("Seleccione una persona"));
    }

    private void seleccionarPersona(Persona persona) {
        tablaTelefonos.getSelectionModel().clearSelection();
        tablaDirecciones.getSelectionModel().clearSelection();
        txtTelefono.clear();
        txtDireccion.clear();
        if (persona == null) {
            tablaTelefonos.getItems().clear();
            tablaDirecciones.getItems().clear();
            cmbDireccionesExistentes.getItems().clear();
            return;
        }
        txtNombre.setText(persona.getNombre());
        ejecutar(() -> recargarDetalles(persona.getId()));
    }

    private void guardarPersona() {
        ejecutar(() -> {
            String nombre = ValidationService.validarNombre(txtNombre.getText());
            String telefono = txtTelefonoInicial.getText().isBlank()
                    ? null : ValidationService.validarTelefono(txtTelefonoInicial.getText());
            int id = dao.crearPersona(nombre, telefono);
            txtTelefonoInicial.clear();
            recargarPersonas(id);
            mostrarInformacion("Persona guardada correctamente.");
        });
    }

    private void actualizarPersona() {
        Persona persona = personaSeleccionada();
        if (persona == null) return;
        ejecutar(() -> {
            dao.actualizarPersona(persona.getId(), ValidationService.validarNombre(txtNombre.getText()));
            recargarPersonas(persona.getId());
        });
    }

    private void eliminarPersona() {
        Persona persona = personaSeleccionada();
        if (persona == null) return;
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminarán también sus teléfonos y sus asociaciones con direcciones. ¿Continuar?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText("Eliminar a " + persona.getNombre());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.YES) return;
        ejecutar(() -> {
            dao.eliminarPersona(persona.getId());
            limpiarSeleccion();
            recargarPersonas(null);
        });
    }

    private void agregarTelefono() {
        Persona persona = personaSeleccionada();
        if (persona == null) return;
        ejecutar(() -> {
            dao.agregarTelefono(persona.getId(), ValidationService.validarTelefono(txtTelefono.getText()));
            txtTelefono.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void actualizarTelefono() {
        Persona persona = personaSeleccionada();
        Telefono telefono = tablaTelefonos.getSelectionModel().getSelectedItem();
        if (persona == null || telefono == null) {
            mostrarAdvertencia("Seleccione el teléfono que desea actualizar.");
            return;
        }
        ejecutar(() -> {
            dao.actualizarTelefono(telefono.getId(), ValidationService.validarTelefono(txtTelefono.getText()));
            txtTelefono.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void eliminarTelefono() {
        Persona persona = personaSeleccionada();
        Telefono telefono = tablaTelefonos.getSelectionModel().getSelectedItem();
        if (persona == null || telefono == null) {
            mostrarAdvertencia("Seleccione el teléfono que desea eliminar.");
            return;
        }
        ejecutar(() -> {
            dao.eliminarTelefono(telefono.getId());
            txtTelefono.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void crearDireccion() {
        Persona persona = personaSeleccionada();
        if (persona == null) return;
        ejecutar(() -> {
            dao.crearYAsociarDireccion(
                    persona.getId(), ValidationService.validarDireccion(txtDireccion.getText()));
            txtDireccion.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void asociarDireccionExistente() {
        Persona persona = personaSeleccionada();
        Direccion direccion = cmbDireccionesExistentes.getValue();
        if (persona == null || direccion == null) {
            mostrarAdvertencia("Seleccione una dirección existente para compartirla.");
            return;
        }
        ejecutar(() -> {
            dao.asociarDireccion(persona.getId(), direccion.getId());
            cmbDireccionesExistentes.setValue(null);
            recargarDetalles(persona.getId());
        });
    }

    private void actualizarDireccion() {
        Persona persona = personaSeleccionada();
        Direccion direccion = tablaDirecciones.getSelectionModel().getSelectedItem();
        if (persona == null || direccion == null) {
            mostrarAdvertencia("Seleccione la dirección que desea modificar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Si esta dirección está compartida, el cambio aparecerá para todas las personas. ¿Continuar?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText("Modificar dirección compartida");
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        ejecutar(() -> {
            dao.actualizarDireccion(
                    direccion.getId(), ValidationService.validarDireccion(txtDireccion.getText()));
            txtDireccion.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void desasociarDireccion() {
        Persona persona = personaSeleccionada();
        Direccion direccion = tablaDirecciones.getSelectionModel().getSelectedItem();
        if (persona == null || direccion == null) {
            mostrarAdvertencia("Seleccione la dirección que desea quitar de esta persona.");
            return;
        }
        ejecutar(() -> {
            dao.desasociarDireccion(persona.getId(), direccion.getId());
            txtDireccion.clear();
            recargarDetalles(persona.getId());
        });
    }

    private void recargarPersonas(Integer seleccionarId) throws SQLException {
        tablaPersonas.setItems(FXCollections.observableArrayList(dao.listarPersonas()));
        if (seleccionarId != null) {
            tablaPersonas.getItems().stream()
                    .filter(persona -> persona.getId() == seleccionarId)
                    .findFirst().ifPresent(persona -> tablaPersonas.getSelectionModel().select(persona));
        }
    }

    private void recargarDetalles(int personaId) throws SQLException {
        tablaTelefonos.setItems(FXCollections.observableArrayList(dao.listarTelefonos(personaId)));
        tablaDirecciones.setItems(FXCollections.observableArrayList(dao.listarDireccionesDePersona(personaId)));
        cmbDireccionesExistentes.setItems(
                FXCollections.observableArrayList(dao.listarDireccionesNoAsociadas(personaId)));
    }

    private Persona personaSeleccionada() {
        Persona persona = tablaPersonas.getSelectionModel().getSelectedItem();
        if (persona == null) {
            mostrarAdvertencia("Primero seleccione una persona.");
        }
        return persona;
    }

    private void limpiarSeleccion() {
        tablaPersonas.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtTelefonoInicial.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        tablaTelefonos.getItems().clear();
        tablaDirecciones.getItems().clear();
        cmbDireccionesExistentes.getItems().clear();
    }

    private void ejecutar(Accion accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException exception) {
            mostrarAdvertencia(exception.getMessage());
        } catch (SQLException exception) {
            mostrarError("Error de base de datos", exception.getMessage());
        } catch (Exception exception) {
            mostrarError("Error inesperado", exception.getMessage());
        }
    }

    private void mostrarInformacion(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK).showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje == null ? titulo : mensaje, ButtonType.OK);
        alert.setHeaderText(titulo);
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface Accion {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
