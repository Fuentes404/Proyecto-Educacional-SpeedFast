package services;

import dao.ConexionBD;
import dao.EntregaDAO;
import dao.PedidoDAO;
import interfaces.Cancelable;
import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// - Función: Controla los pedidos (crear, editar, cancelar y eliminar) y aplica sus reglas del negocio
public class ControladorPedidos {

    // — Dependencias
    // Acceso a la base de datos
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    // — Métodos de Entrada

    // -- Crear
    // Crea un pedido de comida, devuelve el error o null si todo salio bien
    // El ID es 0 porque lo asigna MySQL al guardar
    public String crearComida(String cliente, String direccion, double distanciaKm, String restaurante, String tiempoPreparacion) {
        return guardar(new PedidoComida(0, cliente, direccion, distanciaKm,
                restaurante, tiempoPreparacion));
    }

    // Crea un pedido de encomienda, devuelve el error o null si todo salio bien
    public String crearEncomienda(String cliente, String direccion, double distanciaKm, double peso, double volumen) {
        return guardar(new PedidoEncomienda(0, cliente, direccion, distanciaKm, peso, volumen));
    }

    // Crea un pedido express, devuelve el error o null si todo salio bien
    public String crearExpress(String cliente, String direccion, double distanciaKm, String tienda) {
        return guardar(new PedidoExpress(0, cliente, direccion, distanciaKm, tienda));
    }

    // -- Editar
    // Edita un pedido de comida, devuelve el error o null si todo salio bien
    public String editarComida(int id, String cliente, String direccion, double distanciaKm, String restaurante, String tiempoPreparacion) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            String error = validarEditable(pedido);
            if (error != null) {
                return error;
            }
            if (!(pedido instanceof PedidoComida)) {
                return "El pedido no es de tipo comida.";
            }
            PedidoComida comida = (PedidoComida) pedido;
            actualizarBase(comida, cliente, direccion, distanciaKm);
            comida.setRestaurante(restaurante);
            comida.setTiempoPreparacion(tiempoPreparacion);
            pedidoDAO.actualizar(comida);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // Edita un pedido de encomienda, devuelve el error o null si todo salio bien
    public String editarEncomienda(int id, String cliente, String direccion, double distanciaKm, double peso, double volumen) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            String error = validarEditable(pedido);
            if (error != null) {
                return error;
            }
            if (!(pedido instanceof PedidoEncomienda)) {
                return "El pedido no es de tipo encomienda.";
            }
            PedidoEncomienda encomienda = (PedidoEncomienda) pedido;
            actualizarBase(encomienda, cliente, direccion, distanciaKm);
            encomienda.setPeso(peso);
            encomienda.setVolumen(volumen);
            pedidoDAO.actualizar(encomienda);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // Edita un pedido express, devuelve el error o null si todo salio bien
    public String editarExpress(int id, String cliente, String direccion, double distanciaKm, String tienda) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            String error = validarEditable(pedido);
            if (error != null) {
                return error;
            }
            if (!(pedido instanceof PedidoExpress)) {
                return "El pedido no es de tipo express.";
            }
            PedidoExpress express = (PedidoExpress) pedido;
            actualizarBase(express, cliente, direccion, distanciaKm);
            express.setTienda(tienda);
            pedidoDAO.actualizar(express);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Cancelar
    // Cancela un pedido pendiente, devuelve el error o null si todo salio bien
    // Solo cancelan los pedidos que cumplen el contrato Cancelable (comida y encomienda)
    // Si se cancela, el aviso recibe el mensaje que devuelve cancelar()
    public String cancelar(int id, Consumer<String> aviso) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido == null) {
                return "El pedido no existe.";
            }
            // El contrato decide: si el pedido no es Cancelable, no se cancela
            if (!(pedido instanceof Cancelable)) {
                return "Este tipo de pedido no se puede cancelar.";
            }
            if (pedido.getEstado() == EstadoPedido.CANCELADO) {
                return "El pedido ya está cancelado.";
            }
            if (pedido.getEstado() == EstadoPedido.EN_REPARTO) {
                return "No se puede cancelar un pedido que está en reparto.";
            }
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                return "No se puede cancelar un pedido ya entregado.";
            }

            // Se guarda el nuevo estado en el pedido y en su entrega (si tiene)
            pedidoDAO.actualizarEstado(id, EstadoPedido.CANCELADO);
            entregaDAO.actualizarEstadoPorPedido(id, EstadoPedido.CANCELADO);

            // Mensaje propio de cada tipo de pedido
            aviso.accept(((Cancelable) pedido).cancelar());
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Eliminar
    // Elimina un pedido, devuelve el error o null si todo salio bien
    public String eliminar(int id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido == null) {
                return "El pedido no existe.";
            }
            // Un pedido entregado queda como historial, no se elimina
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                return "No se puede eliminar: un pedido entregado queda como historial.";
            }
            if (pedido.getEstado() == EstadoPedido.EN_REPARTO) {
                return "No se puede eliminar: el pedido está en reparto.";
            }
            // Un pedido pendiente con entrega ya esta asignado a un repartidor
            if (pedido.getEstado() == EstadoPedido.PENDIENTE
                    && entregaDAO.buscarPorPedido(id) != null) {
                return "No se puede eliminar: el pedido ya está asignado a un repartidor.";
            }
            // Los pedidos cancelados se eliminan junto con su entrega
            entregaDAO.eliminarPorPedido(id);
            pedidoDAO.eliminar(id);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // — Métodos de control
    // Revisa que el pedido se pueda modificar, devuelve el error o null si se puede
    private String validarEditable(Pedido pedido) {
        if (pedido == null) {
            return "El pedido no existe.";
        }
        // Un pedido entregado queda congelado con sus datos
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            return "Un pedido entregado no se puede modificar.";
        }
        if (pedido.getEstado() == EstadoPedido.EN_REPARTO) {
            return "No se puede modificar un pedido que está en reparto.";
        }
        // Un pedido cancelado tambien queda congelado
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            return "Un pedido cancelado no se puede modificar.";
        }
        return null;
    }

    // — Métodos de Salida

    // -- Consultas
    // Lista de pedidos para consultas, vacia si falla la base de datos
    public List<Pedido> getPedidos() {
        try {
            return pedidoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return new ArrayList<>();
        }
    }

    // -- Tabla
    // Llena la tabla, los campos que no aplican al tipo quedan en null
    // Devuelve el error o null si todo salio bien
    public String llenarTabla(DefaultTableModel modelo) {
        try {
            List<Pedido> pedidos = pedidoDAO.listarTodos();
            modelo.setRowCount(0);
            for (Pedido pedido : pedidos) {
                Object[] fila = new Object[modelo.getColumnCount()];

                // Columnas base
                fila[0] = pedido.getIdPedido();
                fila[1] = pedido.getTipoPedido();
                fila[2] = pedido.getCliente();
                fila[3] = pedido.getDireccion();
                fila[4] = pedido.getDistanciaKm();
                fila[5] = pedido.getEstado();

                // Columnas propias de cada tipo
                if (pedido instanceof PedidoComida) {
                    PedidoComida comida = (PedidoComida) pedido;
                    fila[6] = comida.getRestaurante();
                    fila[7] = comida.getTiempoPreparacion();
                } else if (pedido instanceof PedidoEncomienda) {
                    PedidoEncomienda encomienda = (PedidoEncomienda) pedido;
                    fila[8] = encomienda.getPeso();
                    fila[9] = encomienda.getVolumen();
                } else if (pedido instanceof PedidoExpress) {
                    PedidoExpress express = (PedidoExpress) pedido;
                    fila[10] = express.getTienda();
                }

                modelo.addRow(fila);
            }
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // — Métodos Auxiliares

    // Guarda en la base de datos cualquier tipo de pedido
    private String guardar(Pedido pedido) {
        try {
            pedidoDAO.guardar(pedido);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // Actualiza los datos que comparten todos los tipos de pedido
    private void actualizarBase(Pedido pedido, String cliente, String direccion, double distanciaKm) {
        pedido.setCliente(cliente);
        pedido.setDireccion(direccion);
        pedido.setDistanciaKm(distanciaKm);
    }
}