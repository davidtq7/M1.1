import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class App extends Application {

    private TableView<Persona> tabla = new TableView<>();
    private ObservableList<Persona> lista = FXCollections.observableArrayList();

    private TextField txtNombre = new TextField();
    private TextField txtDireccion = new TextField();
    private TextField txtTelefono = new TextField();

    // Variable para saber si estamos editando
    private int idSeleccionado = -1;

    // Datos de conexión
    private String url = "jdbc:mariadb://localhost:3306/agenda";
    private String user = "usuario1";
    private String pass = "superpassword";

    public static class Persona {
        private int id;
        private String nombre;
        private String direccion;
        private String telefono;

        public Persona(int id, String nombre, String direccion, String telefono) {
            this.id = id;
            this.nombre = nombre;
            this.direccion = direccion;
            this.telefono = telefono;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDireccion() { return direccion; }
        public String getTelefono() { return telefono; }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestión de Agenda (CRUD)");

        txtNombre.setPromptText("Nombre");
        txtDireccion.setPromptText("Dirección");
        txtTelefono.setPromptText("Teléfono");

        Button btnGuardar = new Button("Guardar Nuevo");
        Button btnModificar = new Button("Actualizar Cambios");
        Button btnEliminar = new Button("Eliminar");

        btnGuardar.setOnAction(e -> guardarPersona());
        btnModificar.setOnAction(e -> actualizarPersona());
        btnEliminar.setOnAction(e -> eliminarPersona());

        // Evento para cargar los datos en las cajas de texto al hacer clic en la tabla
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idSeleccionado = newSelection.getId();
                txtNombre.setText(newSelection.getNombre());
                txtDireccion.setText(newSelection.getDireccion());
                txtTelefono.setText(newSelection.getTelefono());
            }
        });

        HBox formulario = new HBox(10, txtNombre, txtDireccion, txtTelefono, btnGuardar, btnModificar, btnEliminar);

        // Columnas
        TableColumn<Persona, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Persona, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Persona, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        TableColumn<Persona, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tabla.getColumns().addAll(colId, colNombre, colDireccion, colTelefono);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setItems(lista);

        VBox layout = new VBox(15, formulario, tabla);
        layout.setPadding(new Insets(15));

        cargarDatos();

        Scene scene = new Scene(layout, 800, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarDatos() {
        lista.clear();
        limpiarCampos();
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Personas");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String direccion = rs.getString("direccion");

                Statement stmtT = conn.createStatement();
                ResultSet rsT = stmtT.executeQuery("SELECT telefono FROM Telefonos WHERE personaId = " + id);
                String tel = rsT.next() ? rsT.getString("telefono") : "";

                lista.add(new Persona(id, nombre, direccion, tel));
            }
            conn.close();
        } catch (Exception ex) {
            System.out.println("Error al cargar: " + ex.getMessage());
        }
    }

    // ALTA
    private void guardarPersona() {
        if (txtNombre.getText().isEmpty()) return;

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);

            String sqlP = "INSERT INTO Personas (nombre, direccion) VALUES ('" + txtNombre.getText() + "', '" + txtDireccion.getText() + "')";
            Statement stmtP = conn.createStatement();
            stmtP.executeUpdate(sqlP, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = stmtP.getGeneratedKeys();
            if (rs.next()) {
                int idPersona = rs.getInt(1);
                String sqlT = "INSERT INTO Telefonos (personaId, telefono) VALUES (" + idPersona + ", '" + txtTelefono.getText() + "')";
                Statement stmtT = conn.createStatement();
                stmtT.executeUpdate(sqlT);
            }

            conn.close();
            cargarDatos();
        } catch (Exception ex) {
            System.out.println("Error al guardar: " + ex.getMessage());
        }
    }

    // MODIFICACIÓN (CAMBIOS)
    private void actualizarPersona() {
        if (idSeleccionado == -1) return;

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();

            // Actualizar Persona
            stmt.executeUpdate("UPDATE Personas SET nombre='" + txtNombre.getText() + "', direccion='" + txtDireccion.getText() + "' WHERE id=" + idSeleccionado);

            // Actualizar Teléfono
            stmt.executeUpdate("UPDATE Telefonos SET telefono='" + txtTelefono.getText() + "' WHERE personaId=" + idSeleccionado);

            conn.close();
            cargarDatos();
        } catch (Exception ex) {
            System.out.println("Error al actualizar: " + ex.getMessage());
        }
    }

    // BAJA
    private void eliminarPersona() {
        if (idSeleccionado == -1) return;

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Personas WHERE id = " + idSeleccionado);
            conn.close();

            cargarDatos();
        } catch (Exception ex) {
            System.out.println("Error al eliminar: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        idSeleccionado = -1;
        txtNombre.clear();
        txtDireccion.clear();
        txtTelefono.clear();
    }

    