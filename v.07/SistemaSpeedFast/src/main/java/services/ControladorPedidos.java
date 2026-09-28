package services;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import interfaces.Cancelable;
import interfaces.Despachable;
import interfaces.Rastreable;
import model.Entrega;
import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;
import model.Repartidor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Controlador unico de pedidos.
// Los datos viven en MySQL: este controlador coordina las reglas de negocio.
public class ControladorPedidos {

    // Atributos
    // Acceso a la base de datos
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    // Registro de pedidos

    // Crea un pedido de comida y lo guarda en la base de datos.
    // Devuelve: el pedido creado, con el ID que genero MySQL
    public Pedido registrarComida(String cliente, String direccion, double distanciaKm,
                                  String restaurante, String tiempoPreparacion) {
        return registrar(new PedidoComida(0, cliente, direccion, distanciaKm, restaurante, tiempoPreparacion));
    }

    // Crea un pedido de encomienda y lo guarda en la base de datos.
    // Devuelve: el pedido creado, con el ID que genero MySQL
    public Pedido registrarEncomienda(String cliente, String direccion, double distanciaKm,
                                      double peso, double volumen) {
        return registrar(new PedidoEncomienda(0, cliente, direccion, distanciaKm, peso, volumen));
    }

    // Crea un pedido express y lo guarda en la base de datos.
    // Devuelve: el pedido creado, con el ID que genero MySQL
    public Pedido registrarExpress(String cliente, String direccion, double distanciaKm,
                                   String tienda) {
        return registrar(new PedidoExpress(0, cliente, direccion, distanciaKm, tienda));
    }

    // Guarda el pedido con el DAO (el ID se asigna al insertar).
    // Devuelve: el mismo pedido recibido
    // Lanza: IllegalStateException si no se pudo insertar
    private Pedido registrar(Pedido pedido) {
        if (!pedidoDAO.guardar(pedido)) {
            throw new IllegalStateException("No se pudo guardar el pedido.");
        }
        return pedido;
    }

    // Consultas

    // Busca un pedido por ID.
    // Devuelve: el pedido, o null si no existe
    public Pedido buscarPorId(int idPedido) {
        return pedidoDAO.buscarPorId(idPedido);
    }

    // Devuelve: la lista con todos los pedidos
    public List<Pedido> getPedidos() {
        return pedidoDAO.listarTodos();
    }

    // Devuelve: la lista con los pedidos que aun no se entregan ni se cancelan
    public List<Pedido> getPedidosPendientes() {
        List<Pedido> pendientes = new ArrayList<>();
        for (Pedido p : pedidoDAO.listarTodos()) {
            if (p.getEstado() == EstadoPedido.PENDIENTE || p.getEstado() == EstadoPedido.EN_REPARTO) {
                pendientes.add(p);
            }
        }
        return pendientes;
    }

    // Estado del pedido para mostrar en la tabla.
    // Devuelve: "Entregado", "Cancelado", "Asignado a (repartidor)" o "Pendiente"
    public String getEstado(int idPedido) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            return "Desconocido";
        }
        switch (pedido.getEstado()) {
            case ENTREGADO:
                return "Entregado";
            case CANCELADO:
                return "Cancelado";
            case EN_REPARTO:
                String repartidor = entregaDAO.listarAsignaciones().get(idPedido);
                return repartidor != null ? "Asignado a " + repartidor : "En reparto";
            default:
                return "Pendiente";
        }
    }

    // Devuelve: las asignaciones (idPedido -> repartidor) leidas de la tabla entrega, para la simulacion
    public Map<Integer, String> getAsignaciones() {
        return entregaDAO.listarAsignaciones();
    }

    // Asignacion, cancelacion y entrega

    // Asigna un repartidor a un pedido: registra la entrega y pasa el pedido a EN_REPARTO.
    // Devuelve: el mensaje que arma el propio pedido
    public String asignarRepartidor(int idPedido, String nombreRepartidor) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido N°: " + idPedido);
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido N°: " + idPedido + " no admite asignacion (" + pedido.getEstado() + ").");
        }
        Repartidor repartidor = repartidorDAO.buscarPorNombre(nombreRepartidor);
        if (repartidor == null) {
            throw new IllegalArgumentException("No existe el repartidor: " + nombreRepartidor);
        }

        String mensaje = (pedido instanceof Despachable)
                ? ((Despachable) pedido).despachar()
                : pedido.asignarRepartidor(nombreRepartidor);

        // Se guarda primero la entrega: si falla, el estado del pedido no cambia
        entregaDAO.guardar(new Entrega(idPedido, repartidor.getId(), LocalDate.now(), LocalTime.now()));
        pedidoDAO.actualizarEstado(idPedido, EstadoPedido.EN_REPARTO);
        return mensaje;
    }

    // Cancela un pedido (interfaz Cancelable).
    // Devuelve: el mensaje que arma el propio pedido
    public String cancelarPedido(int idPedido) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido N°: " + idPedido);
        }
        if (!(pedido instanceof Cancelable)) {
            throw new IllegalStateException("Los pedidos de tipo " + pedido.getTipoPedido() + " no se pueden cancelar.");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido N°: " + idPedido + " no admite cancelacion (" + pedido.getEstado() + ").");
        }
        String mensaje = ((Cancelable) pedido).cancelar();
        pedidoDAO.actualizarEstado(idPedido, EstadoPedido.CANCELADO);
        return mensaje;
    }

    // Consulta el historial de un pedido (interfaz Rastreable).
    // Devuelve: el mensaje que arma el propio pedido
    public String verHistorial(int idPedido) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido N°: " + idPedido);
        }
        if (!(pedido instanceof Rastreable)) {
            throw new IllegalStateException("Los pedidos de tipo " + pedido.getTipoPedido() + " no tienen historial.");
        }
        return ((Rastreable) pedido).verHistorial();
    }

    // Marca como entregados los pedidos indicados (se usa al terminar la simulacion)
    public void marcarEntregados(List<Pedido> pedidosEntregados) {
        for (Pedido p : pedidosEntregados) {
            p.setEstado(EstadoPedido.ENTREGADO);
            pedidoDAO.actualizarEstado(p.getIdPedido(), EstadoPedido.ENTREGADO);
        }
    }
}