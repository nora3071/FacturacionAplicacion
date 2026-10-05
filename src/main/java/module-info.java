module ni.edu.uam.facturacionapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;

    exports ni.edu.uam.facturacionaplicacion;
    exports ni.edu.uam.facturacionaplicacion.model;
    opens ni.edu.uam.facturacionaplicacion.controller to javafx.fxml;
    opens ni.edu.uam.facturacionaplicacion.model to javafx.base;
}