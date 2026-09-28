package dao;

import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Acceso a datos de la tabla repartidor.
public class RepartidorDAO {

    // Devuelve: todos los repartidores ordenados por ID
    public List<Repartidor> listarTodos() {
        List<Repartidor> repartidores = new ArrayList<>();
        String sql = "SELECT * FROM repartidor ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                repartidores.add(mapear(rs));
            }
            return repartidores;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los repartidores", e);
        }
    }

    // Busca un repartidor por nombre (ignora mayusculas).
    // Devuelve: el repartidor, o null si no existe
    public Repartidor buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM repartidor WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el repartidor " + nombre, e);
        }
    }

    // Guarda un repartidor nuevo y le asigna el ID generado por MySQL.
    // Devuelve: true si se inserto
    public boolean guardar(Repartidor repartidor) {
        String sql = "INSERT INTO repartidor (nombre) VALUES (?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());
            if (ps.executeUpdate() == 0) {
                return false;
            }

            // Lee el ID que genero AUTO_INCREMENT y se lo asigna al objeto
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    repartidor.setId(claves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el repartidor", e);
        }
    }

    // Cuenta las entregas registradas de un repartidor (para saber si se puede eliminar)
    public int contarEntregas(int idRepartidor) {
        String sql = "SELECT COUNT(*) FROM entrega WHERE id_repartidor = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRepartidor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al contar las entregas del repartidor " + idRepartidor, e);
        }
    }

    // Elimina un repartidor por ID.
    // Devuelve: true si se elimino alguna fila
    public boolean eliminar(int idRepartidor) {
        String sql = "DELETE FROM repartidor WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRepartidor);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el repartidor " + idRepartidor, e);
        }
    }

    // Convierte la fila actual del ResultSet en un Repartidor (sin pedidos asignados)
    private Repartidor mapear(ResultSet rs) throws SQLException {
        return new Repartidor(
                rs.getInt("id"),
                rs.getString("nombre"),
                new ArrayList<>()
        );
    }
}