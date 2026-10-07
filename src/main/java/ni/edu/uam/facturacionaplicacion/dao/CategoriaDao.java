package ni.edu.uam.facturacionaplicacion.dao;

import ni.edu.uam.facturacionaplicacion.config.DataBaseConnection;
import ni.edu.uam.facturacionaplicacion.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    // 1. Guardar
    public void guardar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";
        try (
                Connection connection = DataBaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());
            statement.executeUpdate();
        }
    }

    // 2. Listar
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY id ASC";
        try (
                Connection connection = DataBaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(resultSet.getInt("id"));
                categoria.setNombre(resultSet.getString("nombre"));
                categoria.setActiva(resultSet.getBoolean("activa"));
                lista.add(categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Actualizar
    public void actualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";
        try (
                Connection connection = DataBaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());
            statement.setInt(3, categoria.getId());
            statement.executeUpdate();
        }
    }

    // 4. Eliminar
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (
                Connection connection = DataBaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Punto 5: Verificar nombre duplicado al Insertar
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?)";

        try (
                Connection cn = DataBaseConnection.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Punto 5: Excluir la categoría que se está modificando al Actualizar (UPDATE)
    public boolean existeNombreExcluyendoId(String nombre, int idCategoria) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?) AND id != ?";

        try (
                Connection cn = DataBaseConnection.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);
            ps.setInt(2, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Punto 7: Validar eliminación de Categoria (Integridad referencial)
    public boolean tieneProductos(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE categoria_id = ?";

        try (
                Connection cn = DataBaseConnection.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, categoriaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
