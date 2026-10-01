package services;

import dao.ConexionBD;
import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import model.Entrega;
import model.EstadoPedido;
import model.Pedido;
import model.Repartidor;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// - Función: Controla las entregas (asignar, editar, eliminar y enviar) y aplica sus reglas del negocio
public class ControladorEntregas {

    // — Dependencias

    // Acceso a la base de datos
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // — Métodos de Entrada

    // -- Crear
    // Asigna un pedido pendiente a un repartidor, devuelve el error o null si todo salio bien
    public String crear(int idPedido, int idRepartidor) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(idPedido);
            Repartidor repartidor = repartidorDAO.buscarPorId(idRepartidor);

            if (pedido == null) {
                return "El pedido no existe.";
            }
            if (repartidor == null) {
                return "El repartidor no existe.";
            }
            if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
                return "Solo se pueden asignar pedidos pendientes.";
            }
            if (entregaDAO.buscarPorPedido(idPedido) != null) {
                return "El pedido ya está asignado a un repartidor.";
            }

            entregaDAO.guardar(new Entrega(idPedido, idRepartidor, LocalDate.now(), LocalTime.now()));
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Editar
    // Cambia repartidor, fecha y hora de una entrega, devuelve el error o null si todo salio bien
    public String editar(int idEntrega, int idNuevoRepartidor, LocalDate fecha, LocalTime hora) {
        try {
            Entrega entrega = entregaDAO.buscarPorId(idEntrega);
            if (entrega == null) {
                return "La entrega no existe.";
            }
            if (repartidorDAO.buscarPorId(idNuevoRepartidor) == null) {
                return "El repartidor no existe.";
            }

            // Solo se edita mientras el pedido siga pendiente
            Pedido pedido = pedidoDAO.buscarPorId(entrega.getIdPedido());
            if (pedido == null || pedido.getEstado() != EstadoPedido.PENDIENTE) {
                return "Solo se pueden editar entregas de pedidos pendientes.";
            }

            entrega.setIdRepartidor(idNuevoRepartidor);
            entrega.setFecha(fecha);
            entrega.setHora(hora);
            entregaDAO.actualizar(entrega);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Eliminar
    // Deshace la asignacion de un pedido pendiente, devuelve el error o null si todo salio bien
    public String eliminar(int idEntrega) {
        try {
            Entrega entrega = entregaDAO.buscarPorId(idEntrega);
            if (entrega == null) {
                return "La entrega no existe.";
            }

            // Solo se elimina mientras el pedido siga pendiente
            Pedido pedido = pedidoDAO.buscarPorId(entrega.getIdPedido());
            if (pedido == null || pedido.getEstado() != EstadoPedido.PENDIENTE) {
                return "No se puede eliminar: la entrega está en proceso o ya fue realizada.";
            }

            entregaDAO.eliminar(idEntrega);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Enviar
    // Envia los pedidos pendientes de un repartidor, devuelve el error o null si salio bien
    // La salida recibe los mensajes del hilo
    public String enviar(int idRepartidor, Consumer<String> salida) {
        try {
            Repartidor repartidor = repartidorDAO.buscarPorId(idRepartidor);
            if (repartidor == null) {
                return "El repartidor no existe.";
            }

            // Ruta con solo los pedidos que siguen pendientes, se lee desde la base de datos
            List<Pedido> ruta = pedidoDAO.listarPendientesPorRepartidor(idRepartidor);
            if (ruta.isEmpty()) {
                return "El repartidor no tiene pedidos pendientes para enviar.";
            }

            // Los pedidos salen a reparto
            for (Pedido pedido : ruta) {
                pedido.setEstado(EstadoPedido.EN_REPARTO);
                pedidoDAO.actualizarEstado(pedido.getIdPedido(), EstadoPedido.EN_REPARTO);
            }

            // Repartidor de esta ruta, sus mensajes salen por el JTextArea
            Repartidor enRuta = new Repartidor(repartidor.getId(), repartidor.getNombre(), ruta, salida);

            Thread hilo = new Thread(() -> recorrerRuta(enRuta, ruta, salida),
                    "ruta-" + repartidor.getNombre());
            hilo.start();
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // — Métodos de Salida

    // -- Consultas
    // Pedidos que se pueden asignar: pendientes y sin entrega, vacia si falla la base de datos
    public List<Pedido> getPedidosDisponibles() {
        try {
            return pedidoDAO.listarDisponibles();
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return new ArrayList<>();
        }
    }

    // Busca una entrega por su ID, devuelve null si no existe o si falla la base de datos
    public Entrega buscarPorId(int id) {
        try {
            return entregaDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return null;
        }
    }

    // Lista de entregas para consultas, vacia si falla la base de datos
    public List<Entrega> getEntregas() {
        try {
            return entregaDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return new ArrayList<>();
        }
    }

    // -- Tabla
    // Llena la tabla con nombre del repartidor y estado del pedido
    // Devuelve el error o null si todo salio bien
    public String llenarTabla(DefaultTableModel modelo) {
        try {
            List<Entrega> entregas = entregaDAO.listarTodos();

            // Se cargan una sola vez para no consultar la base de datos por cada fila
            Map<Integer, Repartidor> repartidores = new HashMap<>();
            for (Repartidor repartidor : repartidorDAO.listarTodos()) {
                repartidores.put(repartidor.getId(), repartidor);
            }
            Map<Integer, Pedido> pedidos = new HashMap<>();
            for (Pedido pedido : pedidoDAO.listarTodos()) {
                pedidos.put(pedido.getIdPedido(), pedido);
            }

            modelo.setRowCount(0);
            for (Entrega entrega : entregas) {
                Repartidor repartidor = repartidores.get(entrega.getIdRepartidor());
                Pedido pedido = pedidos.get(entrega.getIdPedido());

                modelo.addRow(new Object[]{
                        entrega.getId(),
                        entrega.getIdPedido(),
                        repartidor != null ? repartidor.getNombre() : null,
                        entrega.getFecha(),
                        entrega.getHora(),
                        pedido != null ? pedido.getEstado() : null
                });
            }
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // — Métodos Auxiliares

    // Se ejecuta en el hilo: recorre la ruta y, si no fue interrumpida, marca los pedidos como entregados
    private void recorrerRuta(Repartidor enRuta, List<Pedido> ruta, Consumer<String> salida) {
        enRuta.run();
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            for (Pedido pedido : ruta) {
                pedido.setEstado(EstadoPedido.ENTREGADO);
                pedidoDAO.actualizarEstado(pedido.getIdPedido(), EstadoPedido.ENTREGADO);
            }
        } catch (SQLException e) {
            // Desde el hilo no se puede devolver el error, se muestra en el area de seguimiento
            salida.accept(ConexionBD.mensajeError(e));
        }
    }
}