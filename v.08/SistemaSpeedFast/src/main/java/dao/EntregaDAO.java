package dao;

import model.Entrega;
import model.EstadoPedido;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

// - Función: Operaciones de base de datos de las entregas
// Todos los metodos lanzan SQLException, el controlador la convierte en mensaje
public class EntregaDAO {

    // — Métodos CRUD

    // -- Crear
    // Inserta la entrega (nace PENDIENTE) y le asigna el ID que genero MySQL
    public void guardar(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entrega.setId(claves.getInt(1));
                }
            }
        }
    }

    // -- Actualizar o editar
    // Actualiza repartidor, fecha y hora de la entrega
    public void actualizar(Entrega entrega) throws SQLException {
        String sql = "UPDATE entrega SET id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, entrega.getIdRepartidor());
            ps.setDate(2, Date.valueOf(entrega.getFecha()));
            ps.setTime(3, Time.valueOf(entrega.getHora()));
            ps.setInt(4, entrega.getId());
            ps.executeUpdate();
        }
    }

    // Cambia el estado de la entrega de un pedido, tambien lo usan los hilos de los repartidores
    public void actualizarEstadoPorPedido(int idPedido, EstadoPedido estado) throws SQLException {
        String sql = "UPDATE entrega SET estado = ? WHERE id_pedido = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    // -- Eliminar
    // Elimina una entrega por su ID
    public void eliminar(int id) throws SQLException {
        eliminarDonde("DELETE FROM entrega WHERE id = ?", id);
    }

    // Elimina la entrega de un pedido, la usa ControladorPedidos
    public void eliminarPorPedido(int idPedido) throws SQLException {
        eliminarDonde("DELETE FROM entrega WHERE id_pedido = ?", idPedido);
    }

    // Elimina todas las entregas de un repartidor, la usa ControladorRepartidores
    public void eliminarPorRepartidor(int idRepartidor) throws SQLException {
        eliminarDonde("DELETE FROM entrega WHERE id_repartidor = ?", idRepartidor);
    }

    // — Consultas
    // Busca una entrega por su ID, devuelve null si no existe
    public Entrega buscarPorId(int id) throws SQLException {
        List<Entrega> resultado = consultar("SELECT * FROM entrega WHERE id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // Busca la entrega de un pedido, devuelve null si no tiene
    public Entrega buscarPorPedido(int idPedido) throws SQLException {
        List<Entrega> resultado = consultar("SELECT * FROM entrega WHERE id_pedido = ?", idPedido);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // Devuelve todas las entregas
    public List<Entrega> listarTodos() throws SQLException {
        return consultar("SELECT * FROM entrega ORDER BY id");
    }

    // Revisa si un repartidor tiene entregas con pedidos pendientes o en reparto
    public boolean tieneEntregasEnProceso(int idRepartidor) throws SQLException {
        String sql = "SELECT COUNT(*) FROM entrega e " +
                "JOIN pedido p ON p.id = e.id_pedido " +
                "WHERE e.id_repartidor = ? AND p.estado IN ('PENDIENTE', 'EN_REPARTO')";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idRepartidor);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // — Métodos Auxiliares
    // Ejecuta un DELETE que recibe un solo parametro
    private void eliminarDonde(String sql, int valor) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, valor);
            ps.executeUpdate();
        }
    }

    // Ejecuta una consulta de entregas y arma la lista
    private List<Entrega> consultar(String sql, Object... parametros) throws SQLException {
        List<Entrega> entregas = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entregas.add(new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime()));
                }
            }
        }
        return entregas;
    }
}