package services;

import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Controlador unico de pedidos.
// Todas las ventanas comparten la MISMA instancia,
// asi los datos (listas en memoria) son comunes a toda la aplicacion.
public class ControladorPedidos {

    // Atributos

    // Todos los pedidos registrados
    private final List<Pedido> pedidos = new ArrayList<>();

    // Repartidor asignado a cada pedido (idPedido -> nombre del repartidor)
    private final Map<String, String> asignaciones = new HashMap<>();

    // Ids de los pedidos ya entregados
    private final Set<String> entregados = new HashSet<>();

    // Registro de pedidos

    // Indica si ya existe un pedido con ese ID (ignora mayusculas)
    public boolean existeId(String idPedido) {
        return buscarPorId(idPedido) != null;
    }

    // Crea un pedido de comida y lo agrega a la lista.
    // Devuelve: el pedido creado
    // Lanza: IllegalArgumentException si el ID ya existe
    public Pedido registrarComida(String idPedido, String cliente, String direccion, double distanciaKm,
                                  String restaurante, String tiempoPreparacion) {
        return registrar(new PedidoComida(idPedido, cliente, direccion, distanciaKm, restaurante, tiempoPreparacion));
    }

    // Crea un pedido de encomienda y lo agrega a la lista.
    // Devuelve: el pedido creado
    // Lanza: IllegalArgumentException si el ID ya existe
    public Pedido registrarEncomienda(String idPedido, String cliente, String direccion, double distanciaKm,
                                      double peso, double volumen) {
        return registrar(new PedidoEncomienda(idPedido, cliente, direccion, distanciaKm, peso, volumen));
    }

    // Crea un pedido express y lo agrega a la lista.
    // Devuelve: el pedido creado
    // Lanza: IllegalArgumentException si el ID ya existe
    public Pedido registrarExpress(String idPedido, String cliente, String direccion, double distanciaKm,
                                   String tienda) {
        return registrar(new PedidoExpress(idPedido, cliente, direccion, distanciaKm, tienda));
    }

    // Agrega el pedido a la lista si su ID no esta repetido.
    // Devuelve: el mismo pedido recibido
    // Lanza: IllegalArgumentException si el ID ya existe
    private Pedido registrar(Pedido pedido) {
        if (existeId(pedido.getIdPedido())) {
            throw new IllegalArgumentException("Ya existe un pedido con el ID " + pedido.getIdPedido());
        }
        pedidos.add(pedido);
        return pedido;
    }

    // Consultas

    // Busca un pedido por ID (ignora mayusculas y espacios sobrantes).
    // Devuelve: el pedido, o null si no existe
    public Pedido buscarPorId(String idPedido) {
        if (idPedido == null) {
            return null;
        }
        for (Pedido p : pedidos) {
            if (p.getIdPedido().equalsIgnoreCase(idPedido.trim())) {
                return p;
            }
        }
        return null;
    }

    // Devuelve: copia de la lista con todos los pedidos
    public List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    // Devuelve: copia de la lista con los pedidos que aun no se entregan
    public List<Pedido> getPedidosPendientes() {
        List<Pedido> pendientes = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!entregados.contains(p.getIdPedido())) {
                pendientes.add(p);
            }
        }
        return pendientes;
    }

    // Estado del pedido para mostrar en la tabla.
    // Devuelve: "Entregado", "Asignado a (repartidor)" o "Pendiente"
    public String getEstado(String idPedido) {
        if (entregados.contains(idPedido)) {
            return "Entregado";
        }
        String repartidor = asignaciones.get(idPedido);
        if (repartidor != null) {
            return "Asignado a " + repartidor;
        }
        return "Pendiente";
    }

    // Devuelve: copia de las asignaciones (idPedido -> repartidor) para usar en la simulacion
    public Map<String, String> getAsignaciones() {
        return new HashMap<>(asignaciones);
    }

    // Asignacion y entrega

    // Asigna un repartidor a un pedido y guarda la asignacion.
    // Devuelve: el mensaje que arma el propio pedido (sobrecarga)
    // Lanza: IllegalArgumentException si el pedido no existe
    //        IllegalStateException si el pedido ya fue entregado
    public String asignarRepartidor(String idPedido, String nombreRepartidor) {
        Pedido pedido = buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido N°: " + idPedido);
        }
        if (entregados.contains(pedido.getIdPedido())) {
            throw new IllegalStateException("El pedido N°: " + idPedido + " ya fue entregado.");
        }
        asignaciones.put(pedido.getIdPedido(), nombreRepartidor);
        return pedido.asignarRepartidor(nombreRepartidor);
    }

    // Marca como entregados los pedidos indicados (se usa al terminar la simulacion)
    public void marcarEntregados(List<Pedido> pedidosEntregados) {
        for (Pedido p : pedidosEntregados) {
            entregados.add(p.getIdPedido());
        }
    }
}