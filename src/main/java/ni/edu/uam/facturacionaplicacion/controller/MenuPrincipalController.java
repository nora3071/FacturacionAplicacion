package ni.edu.uam.facturacionaplicacion.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ni.edu.uam.facturacionaplicacion.util.FacturacionApplication;

public class MenuPrincipalController {

    @FXML
    private Button btnCategorias;

    @FXML
    private Button btnProductos;

    @FXML
    private void abrirCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FacturacionApplication.class.getResource("/ni/edu/uam/facturacionaplicacion/categoria-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 850, 600);
            Stage stage = new Stage();
            stage.setTitle("Gestión de Categorías");
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);

            stage.initModality(Modality.WINDOW_MODAL);
            Stage ventanaPrincipal = (Stage) btnCategorias.getScene().getWindow();
            stage.initOwner(ventanaPrincipal);

            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de Categorías:");
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirProductos() {
        try {
            // Se agregó /fxml/ a la ruta para coincidir con la carpeta en resources
            FXMLLoader loader = new FXMLLoader(
                    FacturacionApplication.class.getResource("/ni/edu/uam/facturacionaplicacion/producto-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 850, 600);
            Stage stage = new Stage();
            stage.setTitle("Gestión de Productos");
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);

            stage.initModality(Modality.WINDOW_MODAL);
            Stage ventanaPrincipal = (Stage) btnProductos.getScene().getWindow();
            stage.initOwner(ventanaPrincipal);

            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de Productos:");
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
