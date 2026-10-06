package ni.edu.uam.facturacionaplicacion.model;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Integer id;
    private String codigo;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precio;
    private int existencia;
    private String rutaImagen;
    private boolean activo;
}
