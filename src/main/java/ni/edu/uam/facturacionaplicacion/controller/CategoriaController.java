package ni.edu.uam.facturacionaplicacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacionapp.dao.CategoriaDao;
import ni.edu.uam.facturacionapp.model.Categoria;

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
        // Se cambió obtenerTodas() por listar()
        listaCategorias = FXCollections.observableArrayList(categoriaDao.listar());
        tblCategorias.setItems(listaCategorias);
    }

    @FXML
    private void guardarCategoria() {
        if (txtNombre.getText().isEmpty()) return;

        Categoria nueva = new Categoria();
        nueva.setNombre(txtNombre.getText());

        // Se cambió insertar() por guardar()
        categoriaDao.guardar(nueva);
        cargarDatos();
        limpiarCampos();
    }

    @FXML
    private void actualizarCategoria() {
        if (categoriaSeleccionada == null || txtNombre.getText().isEmpty()) return;

        categoriaSeleccionada.setNombre(txtNombre.getText());

        // Asegúrate de que este método se llame actualizar en tu CategoriaDao
        categoriaDao.actualizar(categoriaSeleccionada);
        cargarDatos();
        limpiarCampos();
    }

    @FXML
    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) return;

        // Asegúrate de que este método se llame eliminar en tu CategoriaDao
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
