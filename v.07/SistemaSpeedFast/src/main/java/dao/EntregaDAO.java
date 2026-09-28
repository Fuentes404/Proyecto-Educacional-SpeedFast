package dao;

import model.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Acceso a datos de la tabla entrega (une un pedido con un repartidor).
public class EntregaDAO {

    // Registra la relacion entre un pedido y un repartidor y le asigna el ID generado por MySQL.
    // Devuelve: true si se inserto
    public boolean guardar(Entrega entrega) {
        String sql = "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));

            if (ps.executeUpdate() == 0) {
                return false;
            }

            // Lee el ID que genero AUTO_INCREMENT y se lo asigna al objeto
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entrega.setId(claves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la entrega", e);
        }
    }

    // Devuelve: todas las entregas registradas, ordenadas por ID
    public List<Entrega> listarTodas() {
        List<Entrega> entregas = new ArrayList<>();
        String sql = "SELECT * FROM entrega ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                entregas.add(mapear(rs));
            }
            return entregas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las entregas", e);
        }
    }

    // Devuelve: idPedido -> nombre del repartidor de su entrega mas reciente
    // (se lee ordenado por ID, asi la ultima entrega de cada pedido es la que queda)
    public Map<Integer, String> listarAsignaciones() {
        Map<Integer, String> asignaciones = new HashMap<>();
        String sql = """
                SELECT e.id_pedido, r.nombre
                FROM entrega e
                JOIN repartidor r ON r.id = e.id_repartidor
                ORDER BY e.id
                """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                asignaciones.put(rs.getInt("id_pedido"), rs.getString("nombre"));
            }
            return asignaciones;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las asignaciones", e);
        }
    }

    // Convierte la fila actual del ResultSet en una Entrega
    private Entrega mapear(ResultSet rs) throws SQLException {
        return new Entrega(
                rs.getInt("id"),
                rs.getInt("id_pedido"),
                rs.getInt("id_repartidor"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime()
        );
    }
}