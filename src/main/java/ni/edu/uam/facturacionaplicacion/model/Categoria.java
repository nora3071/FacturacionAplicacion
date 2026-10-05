package ni.edu.uam.facturacionaplicacion.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activa;

    @Override
    public String toString() {
        return this.nombre; // Devuelve el nombre para que el ComboBox lo muestre correctamente
    }
}