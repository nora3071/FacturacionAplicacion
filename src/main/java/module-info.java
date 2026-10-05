module ni.edu.uam.facturacionaplicacion {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.facturacionaplicacion to javafx.fxml;
    exports ni.edu.uam.facturacionaplicacion;
}