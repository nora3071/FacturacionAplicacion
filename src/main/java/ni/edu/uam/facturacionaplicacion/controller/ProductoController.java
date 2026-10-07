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
import java.sql.SQLException;
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

    // Punto 17: Método para validar y construir el objeto Producto
    private Producto obtenerProductoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            txtCodigo.requestFocus();
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (nombre.isEmpty()) {
            txtNombre.requestFocus();
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        if (categoria == null) {
            cmbCategoria.requestFocus();
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser numérico.");
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia debe ser un número entero.");
        }

        if (existencia < 0) {
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        return new Producto(
                null,
                codigo,
                nombre,
                categoria,
                precio,
                existencia,
                rutaImagen,
                chkActivo.isSelected()
        );
    }

    // Punto 18: Guardar Producto con manejo de excepciones
    @FXML
    private void guardar() {
        try {
            Producto producto = obtenerProductoFormulario();

            if (productoDao.existeCodigo(producto.getCodigo())) {
                mostrarAdvertencia("Código duplicado", "Ya existe un producto con ese código.");
                txtCodigo.requestFocus();
                return;
            }

            productoDao.guardar(producto);

            mostrarInfo("Producto registrado", "La información fue almacenada correctamente.");
            cargarProductos();
            limpiar();

        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible registrar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (productoSeleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Seleccione un producto de la tabla para actualizar.");
            return;
        }

        try {
            Producto productoEditado = obtenerProductoFormulario();

            if (productoDao.existeCodigoExcluyendoId(productoEditado.getCodigo(), productoSeleccionado.getId())) {
                mostrarAdvertencia("Código duplicado", "Ya existe un producto con ese código.");
                txtCodigo.requestFocus();
                return;
            }

            productoSeleccionado.setCodigo(productoEditado.getCodigo());
            productoSeleccionado.setNombre(productoEditado.getNombre());
            productoSeleccionado.setCategoria(productoEditado.getCategoria());
            productoSeleccionado.setPrecio(productoEditado.getPrecio());
            productoSeleccionado.setExistencia(productoEditado.getExistencia());
            productoSeleccionado.setRutaImagen(rutaImagen);
            productoSeleccionado.setActivo(chkActivo.isSelected());

            productoDao.actualizar(productoSeleccionado);

            mostrarInfo("Producto actualizado", "La información fue actualizada correctamente.");
            cargarProductos();
            limpiar();

        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible actualizar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Seleccione un producto para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea eliminar este producto?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            try {
                productoDao.eliminar(productoSeleccionado.getId());
                mostrarInfo("Producto eliminado", "El producto se eliminó correctamente.");
                cargarProductos();
                limpiar();
            } catch (SQLException e) {
                mostrarError("Error de base de datos", "No fue posible eliminar el producto.");
                System.err.println(e.getMessage());
            }
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