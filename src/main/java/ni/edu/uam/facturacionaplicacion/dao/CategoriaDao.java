package ni.edu.uam.facturacionaplicacion.dao;

import ni.edu.uam.facturacionapp.config.DatabaseConnection;
import ni.edu.uam.facturacionapp.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    // 1. Método para Crear (Insertar)
    public void guardar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Método para Leer (Listar todas las categorías)
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY id ASC";
        try (
                Connection connection = DatabaseConnection.getConnection();
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

    // 3. Método para Actualizar (Modificar)
    public void actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());
            statement.setInt(3, categoria.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Método para Eliminar (Borrar)
    public void eliminar(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
