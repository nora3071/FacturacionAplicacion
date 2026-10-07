package ni.edu.uam.facturacionaplicacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ni.edu.uam.facturacionaplicacion.dao.CategoriaDao;
import ni.edu.uam.facturacionaplicacion.dao.ProductoDao;
import ni.edu.uam.facturacionaplicacion.model.Categoria;
import ni.edu.uam.facturacionaplicacion.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class ProductoController {

    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia, txtBuscar;
    @FXML private ComboBox<Categoria> cmbCategoria, cmbFiltroCategoria;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private TableView<Producto> tblProductos;

    @FXML private TableColumn<Producto, String> colCodigo, colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;
    private String rutaImagen;
    private Producto productoSeleccionado;

    private final ProductoDao productoDao = new ProductoDao();
    private final CategoriaDao categoriaDao = new CategoriaDao();

    @FXML
    private void initialize() {
        // Configurar columnas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Cargar Categorías desde la Base de Datos
        List<Categoria> listaCategorias = categoriaDao.listar();
        cmbCategoria.setItems(FXCollections.observableArrayList(listaCategorias));

        // Cargar combos de filtro
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbFiltroEstado.setValue("Todos");

        ObservableList<Categoria> catsFiltro = FXCollections.observableArrayList();
        catsFiltro.add(new Categoria(0, "Todas", true));
        catsFiltro.addAll(listaCategorias);
        cmbFiltroCategoria.setItems(catsFiltro);
        cmbFiltroCategoria.setValue(catsFiltro.get(0));

        // Inicializar la FilteredList
        productosFiltrados = new FilteredList<>(productos, p -> true);
        tblProductos.setItems(productosFiltrados);
        chkActivo.setSelected(true);

        // Listeners para ejecutar filtros automáticos
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        cmbFiltroEstado.valueProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        cmbFiltroCategoria.valueProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());

        cargarProductos();
    }

    private void cargarProductos() {
        productos.clear();
        productos.addAll(productoDao.listar());
    }

    private void aplicarFiltros() {
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String estadoFiltro = cmbFiltroEstado.getValue() == null ? "Todos" : cmbFiltroEstado.getValue();
        Categoria catFiltro = cmbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(prod -> {
            boolean coincideBusqueda = busqueda.isEmpty()
                    || prod.getCodigo().toLowerCase().contains(busqueda)
                    || prod.getNombre().toLowerCase().contains(busqueda)
                    || (prod.getCategoria() != null && prod.getCategoria().getNombre().toLowerCase().contains(busqueda));

            boolean coincideEstado = true;
            if ("Activos".equalsIgnoreCase(estadoFiltro)) {
                coincideEstado = prod.isActivo();
            } else if ("Inactivos".equalsIgnoreCase(estadoFiltro)) {
                coincideEstado = !prod.isActivo();
            }

            boolean coincideCategoria = true;
            if (catFiltro != null && catFiltro.getId() != null && catFiltro.getId() != 0) {
                coincideCategoria = prod.getCategoria() != null && prod.getCategoria().getId().equals(catFiltro.getId());
            }

            return coincideBusqueda && coincideEstado && coincideCategoria;
        });
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cmbFiltroEstado.setValue("Todos");
        if (!cmbFiltroCategoria.getItems().isEmpty()) {
            cmbFiltroCategoria.setValue(cmbFiltroCategoria.getItems().get(0));
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void seleccionarProducto() {
        productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtCodigo.setText(productoSeleccionado.getCodigo());
            txtNombre.setText(productoSeleccionado.getNombre());
            txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
            txtExistencia.setText(String.valueOf(productoSeleccionado.getExistencia()));
            chkActivo.setSelected(productoSeleccionado.isActivo());

            if (productoSeleccionado.getCategoria() != null) {
                for (Categoria cat : cmbCategoria.getItems()) {
                    if (cat.getId().equals(productoSeleccionado.getCategoria().getId())) {
                        cmbCategoria.setValue(cat);
                        break;
                    }
                }
            }

            rutaImagen = productoSeleccionado.getRutaImagen();
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                try {
                    imgProducto.setImage(new Image(rutaImagen));
                } catch (Exception e) {
                    imgProducto.setImage(null);
                }
            } else {
                imgProducto.setImage(null);
            }
        }
    }

    @FXML
    private void guardar() {
        if (!validarEntradas(false)) return;

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());

            Producto nuevoProducto = new Producto(null, txtCodigo.getText().trim(),
                    txtNombre.getText().trim(), cmbCategoria.getValue(), precio,
                    existencia, rutaImagen, chkActivo.isSelected());

            productoDao.guardar(nuevoProducto);
            mensaje(Alert.AlertType.INFORMATION, "Producto guardado exitosamente.");
            limpiar();
            cargarProductos();

        } catch (Exception e) {
            mensaje(Alert.AlertType.ERROR, "Ocurrió un error al procesar la información.");
        }
    }

    @FXML
    private void actualizar() {
        if (productoSeleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto de la tabla para actualizar.");
            return;
        }

        if (!validarEntradas(true)) return;

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());

            productoSeleccionado.setCodigo(txtCodigo.getText().trim());
            productoSeleccionado.setNombre(txtNombre.getText().trim());
            productoSeleccionado.setCategoria(cmbCategoria.getValue());
            productoSeleccionado.setPrecio(precio);
            productoSeleccionado.setExistencia(existencia);
            productoSeleccionado.setRutaImagen(rutaImagen);
            productoSeleccionado.setActivo(chkActivo.isSelected());

            productoDao.actualizar(productoSeleccionado);
            mensaje(Alert.AlertType.INFORMATION, "Producto actualizado exitosamente.");
            limpiar();
            cargarProductos();

        } catch (Exception e) {
            mensaje(Alert.AlertType.ERROR, "Ocurrió un error al actualizar.");
        }
    }

    @FXML
    private void eliminar() {
        if (productoSeleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea eliminar este producto?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText("Confirmación de Eliminación");
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            productoDao.eliminar(productoSeleccionado.getId());
            mensaje(Alert.AlertType.INFORMATION, "Producto eliminado.");
            limpiar();
            cargarProductos();
        }
    }

    @FXML
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarEntradas(boolean esEdicion) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String existenciaStr = txtExistencia.getText().trim();

        if (codigo.isEmpty()) {
            mensaje(Alert.AlertType.WARNING, "El código es obligatorio.");
            return false;
        }

        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Debe seleccionar una categoría.");
            return false;
        }

        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                if (!esEdicion || (productoSeleccionado != null && !p.getId().equals(productoSeleccionado.getId()))) {
                    mensaje(Alert.AlertType.WARNING, "El código '" + codigo + "' ya existe. No se permiten duplicados.");
                    return false;
                }
            }
        }

        try {
            BigDecimal precio = new BigDecimal(precioStr);
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mensaje(Alert.AlertType.WARNING, "El precio debe ser mayor que cero.");
                return false;
            }
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.WARNING, "El precio debe ser un número entero o decimal válido.");
            return false;
        }

        try {
            int existencia = Integer.parseInt(existenciaStr);
            if (existencia < 0) {
                mensaje(Alert.AlertType.WARNING, "La existencia no puede ser negativa.");
                return false;
            }
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.WARNING, "La existencia debe ser un número entero válido.");
            return false;
        }

        return true;
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        Alert alert = new Alert(tipo, texto, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}