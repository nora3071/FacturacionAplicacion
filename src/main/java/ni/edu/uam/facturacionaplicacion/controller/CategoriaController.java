package ni.edu.uam.facturacionaplicacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacionaplicacion.dao.CategoriaDao;
import ni.edu.uam.facturacionaplicacion.model.Categoria;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private TableView<Categoria> tblCategorias;

    private CategoriaDao categoriaDao = new CategoriaDao();
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
        System.out.println("--> Se presionó el botón Guardar");

        if (txtNombre.getText().isEmpty()) {
            System.out.println("--> El campo de texto está vacío");
            return;
        }

        Categoria nueva = new Categoria();
        nueva.setNombre(txtNombre.getText());
        nueva.setActiva(true);

        System.out.println("--> Intentando guardar la categoría: " + nueva.getNombre());
        categoriaDao.guardar(nueva);

        System.out.println("--> Guardado intentado, recargando datos...");
        cargarDatos();
        limpiarCampos();
    }

    @FXML
    private void actualizarCategoria() {
        if (categoriaSeleccionada == null || txtNombre.getText().isEmpty()) return;

        categoriaSeleccionada.setNombre(txtNombre.getText());

        categoriaDao.actualizar(categoriaSeleccionada);
        cargarDatos();
        limpiarCampos();
    }

    @FXML
    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) return;

        categoriaDao.eliminar(categoriaSeleccionada.getId());
        cargarDatos();
        limpiarCampos();
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
}
