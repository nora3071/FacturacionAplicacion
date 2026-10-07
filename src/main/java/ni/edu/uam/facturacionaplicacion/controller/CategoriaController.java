package ni.edu.uam.facturacionaplicacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacionaplicacion.dao.CategoriaDao;
import ni.edu.uam.facturacionaplicacion.model.Categoria;

import java.sql.SQLException;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private TableView<Categoria> tblCategorias;

    private final CategoriaDao categoriaDao = new CategoriaDao();
    private ObservableList<Categoria> listaCategorias;
    private Categoria categoriaSeleccionada;

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        listaCategorias = FXCollections.observableArrayList(categoriaDao.listar());
        tblCategorias.setItems(listaCategorias);
    }

    @FXML
    private void guardarCategoria() {
        if (!validarCategoria(null)) {
            return;
        }

        try {
            Categoria nueva = new Categoria();
            nueva.setNombre(txtNombre.getText().trim());
            nueva.setActiva(true);

            categoriaDao.guardar(nueva);
            mostrarInfo("Éxito", "Categoría guardada correctamente.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarError("Error de BD", "Error al guardar en la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarCategoria() {
        // Punto 6: Comprobar selección previa
        categoriaSeleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una categoría", "Debe seleccionar la categoría que desea actualizar.");
            return;
        }

        // Punto 6: Validar nuevamente el nombre antes de ejecutar el UPDATE
        if (!validarCategoria(categoriaSeleccionada.getId())) {
            return;
        }

        try {
            categoriaSeleccionada.setNombre(txtNombre.getText().trim());

            categoriaDao.actualizar(categoriaSeleccionada);
            mostrarInfo("Éxito", "Categoría actualizada correctamente.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarError("Error de BD", "Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarCategoria() {
        // Punto 4: Para eliminar debe existir una categoría seleccionada
        categoriaSeleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una categoría", "Debe seleccionar la categoría que desea eliminar.");
            return;
        }

        try {
            // Punto 7: Validar que no existan productos asociados
            if (categoriaDao.tieneProductos(categoriaSeleccionada.getId())) {
                mostrarError("Operación Cancelada", "No se puede eliminar la categoría porque tiene productos asociados.");
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar la categoría seleccionada?", ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText(null);
            confirmacion.showAndWait();

            if (confirmacion.getResult() == ButtonType.YES) {
                categoriaDao.eliminar(categoriaSeleccionada.getId());
                mostrarInfo("Éxito", "Categoría eliminada correctamente.");
                cargarDatos();
                limpiarCampos();
            }
        } catch (SQLException e) {
            mostrarError("Error de BD", "Error al intentar eliminar la categoría: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarCategoria() {
        categoriaSeleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada != null) {
            txtNombre.setText(categoriaSeleccionada.getNombre());
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        categoriaSeleccionada = null;
        tblCategorias.getSelectionModel().clearSelection();
    }

    // Punto 4 & 5: Validación centralizada
    private boolean validarCategoria(Integer idExcluir) {
        String nombre = txtNombre.getText().trim();

        // 1 y 2. El nombre no puede estar vacío ni contener únicamente espacios
        if (nombre.isEmpty()) {
            mostrarError("Validación", "El nombre de la categoría es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }

        // 3 y 5. No deberán existir categorías con el mismo nombre
        try {
            boolean existe = (idExcluir == null)
                    ? categoriaDao.existeNombre(nombre)
                    : categoriaDao.existeNombreExcluyendoId(nombre, idExcluir);

            if (existe) {
                mostrarError("Validación", "Ya existe una categoría registrada con el nombre '" + nombre + "'.");
                txtNombre.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            mostrarError("Error de BD", "Error al verificar duplicados: " + e.getMessage());
            return false;
        }

        return true;
    }

    // Métodos para alertas del sistema
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
