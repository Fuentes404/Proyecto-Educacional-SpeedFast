package dao;

import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Acceso a datos de la tabla pedido
public class PedidoDAO {

    // Devuelve: todos los pedidos ordenados por ID
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapear(rs));
            }
            return pedidos;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los pedidos", e);
        }
    }

    // Busca un pedido por ID.
    // Devuelve: el pedido, o null si no existe
    public Pedido buscarPorId(int idPedido) {
        String sql = "SELECT * FROM pedido WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el pedido " + idPedido, e);
        }
    }

    // Guarda un pedido y le asigna el ID generado por MySQL.
    // Las columnas que no corresponden al tipo se guardan como NULL.
    // Devuelve: true si se inserto
    public boolean guardar(Pedido pedido) {
        String sql = """
                INSERT INTO pedido
                (cliente, direccion, distancia_km, tipo, estado,
                 restaurante, tiempo_preparacion, peso, volumen, tienda)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Campos comunes
            ps.setString(1, pedido.getCliente());
            ps.setString(2, pedido.getDireccion());
            ps.setDouble(3, pedido.getDistanciaKm());
            ps.setString(4, pedido.getTipoPedido().name());
            ps.setString(5, pedido.getEstado().name());

            // Campos especificos: por defecto NULL
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
            ps.setNull(8, Types.DECIMAL);
            ps.setNull(9, Types.DECIMAL);
            ps.setNull(10, Types.VARCHAR);

            // Se rellenan solo los del tipo que corresponde
            if (pedido instanceof PedidoComida) {
                PedidoComida c = (PedidoComida) pedido;
                ps.setString(6, c.getRestaurante());
                ps.setString(7, c.getTiempoPreparacion());
            } else if (pedido instanceof PedidoEncomienda) {
                PedidoEncomienda e = (PedidoEncomienda) pedido;
                ps.setDouble(8, e.getPeso());
                ps.setDouble(9, e.getVolumen());
            } else if (pedido instanceof PedidoExpress) {
                ps.setString(10, ((PedidoExpress) pedido).getTienda());
            }

            if (ps.executeUpdate() == 0) {
                return false;
            }

            // Lee el ID que genero AUTO_INCREMENT y se lo asigna al objeto
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    pedido.setIdPedido(claves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el pedido", e);
        }
    }

    // Cambia el estado de un pedido (PENDIENTE, EN_REPARTO, ENTREGADO, CANCELADO).
    // Devuelve: true si se actualizo alguna fila
    public boolean actualizarEstado(int idPedido, EstadoPedido estado) {
        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado del pedido " + idPedido, e);
        }
    }

    // Convierte la fila actual del ResultSet en PedidoComida, PedidoEncomienda o PedidoExpress
    private Pedido mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String cliente = rs.getString("cliente");
        String direccion = rs.getString("direccion");
        double distancia = rs.getDouble("distancia_km");

        Pedido pedido;
        switch (rs.getString("tipo")) {
            case "COMIDA":
                pedido = new PedidoComida(id, cliente, direccion, distancia,
                        rs.getString("restaurante"), rs.getString("tiempo_preparacion"));
                break;
            case "ENCOMIENDA":
                pedido = new PedidoEncomienda(id, cliente, direccion, distancia,
                        rs.getDouble("peso"), rs.getDouble("volumen"));
                break;
            default:
                pedido = new PedidoExpress(id, cliente, direccion, distancia,
                        rs.getString("tienda"));
                break;
        }
        // El constructor deja el estado en PENDIENTE: se restaura el de la BD
        pedido.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        return pedido;
    }
}