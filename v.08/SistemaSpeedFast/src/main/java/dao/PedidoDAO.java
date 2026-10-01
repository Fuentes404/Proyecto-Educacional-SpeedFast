package dao;

import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;
import model.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// - Función: Operaciones de base de datos de los pedidos (los 3 tipos usan la misma tabla)
// Todos los metodos lanzan SQLException, el controlador la convierte en mensaje
public class PedidoDAO {

    // — Constantes
    // Columnas que se leen en todas las consultas de pedidos
    private static final String SELECT_PEDIDO =
            "SELECT p.id, p.tipo, p.cliente, p.direccion, p.distancia_km, p.estado, " +
                    "p.restaurante, p.tiempo_preparacion, p.peso, p.volumen, p.tienda " +
                    "FROM pedido p ";

    // — Métodos CRUD

    // -- Crear
    // Inserta el pedido y le asigna el ID que genero MySQL
    public void guardar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (tipo, cliente, direccion, distancia_km, restaurante, " +
                "tiempo_preparacion, peso, volumen, tienda, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(ps, pedido);
            ps.setString(10, pedido.getEstado().name());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    pedido.setIdPedido(claves.getInt(1));
                }
            }
        }
    }

    // -- Actualizar o editar
    // Actualiza los datos del pedido, el estado tiene su propio metodo
    public void actualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedido SET tipo = ?, cliente = ?, direccion = ?, distancia_km = ?, " +
                "restaurante = ?, tiempo_preparacion = ?, peso = ?, volumen = ?, tienda = ? " +
                "WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarDatos(ps, pedido);
            ps.setInt(10, pedido.getIdPedido());
            ps.executeUpdate();
        }
    }

    // Cambia solo el estado, tambien lo usan los hilos de los repartidores
    public void actualizarEstado(int idPedido, EstadoPedido estado) throws SQLException {
        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    // -- Eliminar
    // Elimina el pedido por su ID
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pedido WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // — Consultas: búsquedas específicas
    // Busca un pedido por su ID, devuelve null si no existe
    public Pedido buscarPorId(int id) throws SQLException {
        List<Pedido> resultado = consultar(SELECT_PEDIDO + "WHERE p.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // Devuelve todos los pedidos
    public List<Pedido> listarTodos() throws SQLException {
        return consultar(SELECT_PEDIDO + "ORDER BY p.id");
    }

    // Pedidos que se pueden asignar: pendientes y sin entrega
    public List<Pedido> listarDisponibles() throws SQLException {
        return consultar(SELECT_PEDIDO +
                "WHERE p.estado = 'PENDIENTE' " +
                "AND NOT EXISTS (SELECT 1 FROM entrega e WHERE e.id_pedido = p.id) " +
                "ORDER BY p.id");
    }

    // Pedidos pendientes asignados a un repartidor, en el orden en que se asignaron
    public List<Pedido> listarPendientesPorRepartidor(int idRepartidor) throws SQLException {
        return consultar(SELECT_PEDIDO +
                "JOIN entrega e ON e.id_pedido = p.id " +
                "WHERE e.id_repartidor = ? AND p.estado = 'PENDIENTE' " +
                "ORDER BY e.id", idRepartidor);
    }

    // — Métodos Auxiliares

    // Ejecuta una consulta de pedidos y arma la lista con el tipo correcto de cada uno
    private List<Pedido> consultar(String sql, Object... parametros) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(construir(rs));
                }
            }
        }
        return pedidos;
    }

    // Crea el pedido del tipo que indica la fila y le restaura su estado
    private Pedido construir(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String cliente = rs.getString("cliente");
        String direccion = rs.getString("direccion");
        double distancia = rs.getDouble("distancia_km");

        Pedido pedido;
        switch (TipoPedido.valueOf(rs.getString("tipo"))) {
            case COMIDA:
                pedido = new PedidoComida(id, cliente, direccion, distancia,
                        rs.getString("restaurante"), rs.getString("tiempo_preparacion"));
                break;
            case ENCOMIENDA:
                pedido = new PedidoEncomienda(id, cliente, direccion, distancia,
                        rs.getDouble("peso"), rs.getDouble("volumen"));
                break;
            default:
                pedido = new PedidoExpress(id, cliente, direccion, distancia,
                        rs.getString("tienda"));
        }
        pedido.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        return pedido;
    }

    // Asigna los parametros 1 al 9, comunes al INSERT y al UPDATE
    // Las columnas que no aplican al tipo del pedido se guardan como NULL
    private void asignarDatos(PreparedStatement ps, Pedido pedido) throws SQLException {
        String restaurante = null;
        String tiempoPreparacion = null;
        Double peso = null;
        Double volumen = null;
        String tienda = null;

        if (pedido instanceof PedidoComida) {
            PedidoComida comida = (PedidoComida) pedido;
            restaurante = comida.getRestaurante();
            tiempoPreparacion = comida.getTiempoPreparacion();
        } else if (pedido instanceof PedidoEncomienda) {
            PedidoEncomienda encomienda = (PedidoEncomienda) pedido;
            peso = encomienda.getPeso();
            volumen = encomienda.getVolumen();
        } else if (pedido instanceof PedidoExpress) {
            tienda = ((PedidoExpress) pedido).getTienda();
        }

        ps.setString(1, pedido.getTipoPedido().name());
        ps.setString(2, pedido.getCliente());
        ps.setString(3, pedido.getDireccion());
        ps.setDouble(4, pedido.getDistanciaKm());
        ps.setString(5, restaurante);
        ps.setString(6, tiempoPreparacion);
        ps.setObject(7, peso, Types.DOUBLE);
        ps.setObject(8, volumen, Types.DOUBLE);
        ps.setString(9, tienda);
    }
}