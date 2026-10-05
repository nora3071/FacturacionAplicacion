package ni.edu.uam.facturacionaplicacion.dao;

import ni.edu.uam.facturacionaplicacion.config.DatabaseConnection;
import ni.edu.uam.facturacionaplicacion.model.Categoria;
import ni.edu.uam.facturacionaplicacion.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    public void guardar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.activa AS categoria_activa " +
                "FROM producto p INNER JOIN categoria c ON p.categoria_id = c.id ORDER BY p.id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nombre"),
                        rs.getBoolean("categoria_activa")
                );

                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        categoria,
                        rs.getBigDecimal("precio_venta"),
                        rs.getInt("existencia"),
                        rs.getString("ruta_imagen"),
                        rs.getBoolean("activo")
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, categoria_id = ?, precio_venta = ?, existencia = ?, ruta_imagen = ?, activo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());

            if (producto.getCategoria() != null) {
                stmt.setInt(3, producto.getCategoria().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setBigDecimal(4, producto.getPrecioVenta());
            stmt.setInt(5, producto.getExistencia());
            stmt.setString(6, producto.getRutaImagen());
            stmt.setBoolean(7, producto.isActivo());
            stmt.setInt(8, producto.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(Integer id) {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}