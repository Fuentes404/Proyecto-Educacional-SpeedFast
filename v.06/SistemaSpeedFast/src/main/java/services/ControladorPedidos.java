package services;

import interfaces.Cancelable;
import interfaces.Despachable;
import interfaces.Rastreable;
import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controlador unico de pedidos.
// Todas las ventanas comparten la MISMA instancia,
// asi los datos (listas en memoria) son comunes a toda la aplicacion.
public class ControladorPedidos {

    // Atributos

    // Todos los pedidos registrados
    private final List<Pedido> pedidos = new ArrayList<>();

    // Repartidor asignado a cada pedido (idPedido -> nombre del repartidor)
    private final Map<Integer, String> asignaciones = new HashMap<>();

    // Contador para generar el ID mientras no hay base de datos
    // (luego lo reemplaza el AUTO_INCREMENT de la tabla pedido)
    private int contadorId = 1;

    // Registro de pedidos

    // Crea un pedido de comida y lo agrega a la lista.
    // Devuelve: el pedido creado
    public Pedido registrarComida(String cliente, String direccion, double distanciaKm,
                                  String restaurante, String tiempoPreparacion) {
        return registrar(new PedidoComida(contadorId++, cliente, direccion, distanciaKm, restaurante, tiempoPreparacion));
    }

    // Crea un pedido de encomienda y lo agrega a la lista.
    // Devuelve: el pedido creado
    public Pedido registrarEncomienda(String cliente, String direccion, double distanciaKm,
                                      double peso, double volumen) {
        return registrar(new PedidoEncomienda(contadorId++, cliente, direccion, distanciaKm, peso, volumen));
    }

    // Crea un pedido express y lo agrega a la lista.
    // Devuelve: el pedido creado
    public Pedido registrarExpress(String cliente, String direccion, double distanciaKm,
                                   String tienda) {
        return registrar(new PedidoExpress(contadorId++, cliente, direccion, distanciaKm, tienda));
    }

    // Agrega el pedido a la lista.
    // Devuelve: el mismo pedido recibido
    private Pedido registrar(Pedido pedido) {
        pedidos.add(pedido);
        return pedido;
    }

    // Consultas

    // Busca un pedido por ID.
    // Devuelve: el pedido, o null si no existe
    public Pedido buscarPorId(int idPedido) {
        for (Pedido p : pedidos) {
            if (p.getIdPedido() == idPedido) {
                return p;
            }
        }
        return null;
    }

    // Devuelve: copia de la lista con todos los pedidos
    public List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    // Devuelve: copia de la lista con los pedidos que aun no se entregan ni se cancelan
    public List<Pedido> getPedidosPendientes() {
        List<Pedido> pendientes = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getEstado() == EstadoPedido.PENDIENTE || p.getEstado() == EstadoPedido.EN_REPARTO) {
                pendientes.add(p);
            }
        }
        return pendientes;
    }

    // Estado del pedido para mostrar en la tabla.
    // Devuelve: "Entregado", "Cancelado", "Asignado a (repartidor)" o "Pendiente"
    public String getEstado(int idPedido) {
        Pedido pedido = buscarPorId(idPedido);
        if (pedido == null) {
            return "Desconocido";
        }
        switch (pedido.getEstado()) {
            case ENTREGADO:
                return "Entregado";
            case CANCELADO:
                return "Cancelado";
            case EN_REPARTO:
                String repartidor = asignaciones.get(idPedido);
                return repartidor != null ? "Asignado a " + repartidor : "En reparto";
            default:
                return "Pendiente";
        }
    }

    // Devuelve: copia de las asignaciones (idPedido -> repartidor) para usar en la simulacion
    public Map<Integer, String> getAsignaciones() {
        return new HashMap<>(asignaciones);
    }

    // Asignacion, cancelacion y entrega

    // Asigna un repartidor a un pedido y guarda la asignacion.
    // Si el pedido es Despachable (Express), asignar equivale a despachar.
    // Devuelve: el mensaje que arma el propio pedido
    // Lanza: IllegalArgumentException si el pedido no existe
    //        IllegalStateException si el pedido ya fue entregado o cancelado
    public String asignarRepartidor(int idPedido, String nombreRepartidor) {
        Pedido pedido = buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido N°: " + idPedido);
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido N°: " + idPedido + " no admite asignacion (" + pedido.getEstado() + ").");
        }

        String mensaje = (pedido instanceof Despachable)
                ? ((Despachable) pedido).despachar()
                : pedido.asignarRepartidor(nombreRepartidor);

        pedido.setEstado(EstadoPedido.EN_REPARTO);
        asignaciones.put(idPedido, nombreRepartidor);
        return mensaje;
    }

    // Cancela un pedido (interfaz Cancelable).
    // Devuelve: el mensaje que arma el propio pedido
    // Lanza: IllegalArgumentException si el pedido no existe
    //        IllegalStateException si el tipo no es Cancelable, o ya fue entregado/cancelado
    public String cancelarPedido(int idPedido) {
        Pedido pedido = buscarPorId(idPedido);
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
        pedido.setEstado(EstadoPedido.CANCELADO);
        asignaciones.remove(idPedido);
        return mensaje;
    }

    // Consulta el historial de un pedido (interfaz Rastreable).
    // Devuelve: el mensaje que arma el propio pedido
    // Lanza: IllegalArgumentException si el pedido no existe
    //        IllegalStateException si el tipo no es Rastreable
    public String verHistorial(int idPedido) {
        Pedido pedido = buscarPorId(idPedido);
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
        }
    }
}