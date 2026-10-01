package dao;

import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// - Función: Operaciones de base de datos de los repartidores
// Todos los metodos lanzan SQLException, el controlador la convierte en mensaje
public class RepartidorDAO {

    // — Métodos CRUD

    // -- Crear
    // Inserta el repartidor y le asigna el ID que genero MySQL
    public void guardar(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidor (nombre) VALUES (?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    repartidor.setId(claves.getInt(1));
                }
            }
        }
    }

    // -- Actualizar o editar
    // Actualiza el nombre del repartidor
    public void actualizar(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidor SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());
            ps.executeUpdate();
        }
    }

    // -- Eliminar
    // Elimina el repartidor por su ID
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM repartidor WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // — Consultas
    // Busca un repartidor por su ID, devuelve null si no existe
    public Repartidor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM repartidor WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construir(rs);
                }
            }
        }
        return null;
    }

    // Devuelve todos los repartidores
    public List<Repartidor> listarTodos() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidor ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                repartidores.add(construir(rs));
            }
        }
        return repartidores;
    }

    // — Métodos Auxiliares

    // Crea un Repartidor con la fila actual, sus pedidos se cargan solo cuando hacen falta
    private Repartidor construir(ResultSet rs) throws SQLException {
        return new Repartidor(rs.getInt("id"), rs.getString("nombre"), new ArrayList<>());
    }
}