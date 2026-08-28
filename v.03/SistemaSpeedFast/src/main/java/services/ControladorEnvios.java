package services;

import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;
import interfaces.Cancelable;
import interfaces.Despachable;
import interfaces.Rastreable;

import java.util.ArrayList;

public class ControladorEnvios {

    // Arraylist volatil pedidos
    ArrayList<Pedido> pedidos = new ArrayList<>();

    // ArrayList contenedor del historial de todos los pedidos
    ArrayList<Pedido> pedidosTerminados = new ArrayList<>();

    // Contador interno para generar el numero de pedido automaticamente
    private int contadorId = 1;

    // Metodo auxiliar para generar el ID + 1
    private String generarId() {
        String id = String.valueOf(contadorId);
        contadorId++;
        return id;
    }

    // Sobrecarga: Crear Pedido de Comida
    public void crearPedido(String cliente, String direccion, double distanciaKm, String restaurante, String tiempoPreparacion) {
        String idPedido = generarId();
        Pedido nuevoPedido = new PedidoComida(idPedido, cliente, direccion, distanciaKm, restaurante, tiempoPreparacion);
        nuevoPedido.asignarRepartidor();
        pedidos.add(nuevoPedido);
        System.out.println("Pedido de Comida creado con exito");
    }

    // Sobrecarga: Crear Pedido de Encomienda
    public void crearPedido(String cliente, String direccion, double distanciaKm, double peso, double volumen) {
        String idPedido = generarId();
        Pedido nuevoPedido = new PedidoEncomienda(idPedido, cliente, direccion, distanciaKm, peso, volumen);
        nuevoPedido.asignarRepartidor();
        pedidos.add(nuevoPedido);
        System.out.println("Pedido de Encomienda creado con exito");
    }

    // Sobrecarga: Crear Pedido Express
    public void crearPedido(String cliente, String direccion, double distanciaKm, String tienda) {
        String idPedido = generarId();
        Pedido nuevoPedido = new PedidoExpress(idPedido, cliente, direccion, distanciaKm, tienda);
        nuevoPedido.asignarRepartidor();
        pedidos.add(nuevoPedido);
        System.out.println("Pedido Express creado con exito");
    }

    // Metodo para recorrer el arraylist y mostrar solo los pedidos que se pueden cancelar
    public void verCancelables() {
        boolean hayCancelables = false;

        for (Pedido p : pedidos) {
            if (p instanceof Cancelable) {
                p.mostrarResumen();
                System.out.println("---------------------------------------------------------------------");
                hayCancelables = true;
            }
        }

        if (!hayCancelables) {
            System.out.println("No hay pedidos disponibles para cancelar.");
        }
    }

    // Metodo para cancelar un Pedido mediante numero de pedido
    public void cancelarPedido(String idPedido) {
        Pedido pedidoEncontrado = null;

        for (Pedido p : pedidos) {
            if (p.getIdPedido().equals(idPedido)) {
                pedidoEncontrado = p;
                break;
            }
        }

        if (pedidoEncontrado == null) {
            System.out.println("No se encontro un pedido vigente N°: " + idPedido);
            return;
        }

        // Solo se puede cancelar si el pedido implementa Cancelable
        if (pedidoEncontrado instanceof Cancelable) {
            ((Cancelable) pedidoEncontrado).cancelar();
            pedidosTerminados.add(pedidoEncontrado);
            pedidos.remove(pedidoEncontrado);
            System.out.println("Pedido " + idPedido + " cancelado con exito.");
        } else {
            System.out.println("Este tipo de pedido no se puede cancelar.");
        }
    }

    // Metodo para recorrer el arraylist y mostrar solo los pedidos que se pueden despachar
    public void verDespachables() {
        boolean hayDespachables = false;

        for (Pedido p : pedidos) {
            if (p instanceof Despachable) {
                p.mostrarResumen();
                System.out.println("---------------------------------------------------------------------");
                hayDespachables = true;
            }
        }

        if (!hayDespachables) {
            System.out.println("No hay pedidos disponibles para despachar.");
        }
    }

    // Metodo para despachar un pedido mediante numero de pedido
    public void despacharPedido(String idPedido) {
        Pedido pedidoEncontrado = null;

        for (Pedido p : pedidos) {
            if (p.getIdPedido().equals(idPedido)) {
                pedidoEncontrado = p;
                break;
            }
        }

        if (pedidoEncontrado == null) {
            System.out.println("No se encontro un pedido vigente N°: " + idPedido);
            return;
        }

        // Solo se puede despachar si el pedido implementa Despachable (Express)
        if (pedidoEncontrado instanceof Despachable) {
            ((Despachable) pedidoEncontrado).despachar();
            pedidosTerminados.add(pedidoEncontrado);
            pedidos.remove(pedidoEncontrado);
            System.out.println("Pedido " + idPedido + " despachado con exito.");
        } else {
            System.out.println("Este tipo de pedido no se puede despachar.");
        }
    }

    // Metodo para recorrer el arraylist y mediante instance off mostrar los pedidos vigentes
    public void verVigentes() {
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos vigentes.");
            return;
        }

        for (Pedido p : pedidos) {
            if (p instanceof PedidoComida) {
                p.mostrarResumen();
            } else if (p instanceof PedidoEncomienda) {
                p.mostrarResumen();
            } else if (p instanceof PedidoExpress) {
                p.mostrarResumen();
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    // Metodo para recorrer el arraylist y mostrar el contenido de este
    public void verPedidosTerminados() {
        if (pedidosTerminados.isEmpty()) {
            System.out.println("No hay pedidos en el historial");
            return;
        }

        for (Pedido p : pedidosTerminados) {
            if (p instanceof PedidoComida) {
                p.mostrarResumen();
            } else if (p instanceof PedidoEncomienda) {
                p.mostrarResumen();
            } else if (p instanceof PedidoExpress) {
                p.mostrarResumen();
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    // Metodo para buscar un pedido especifico vigente o en historial
    public void buscarPedido(String idPedido) {
        Pedido pedidoEncontrado = null;

        // Busca primero entre los vigentes
        for (Pedido p : pedidos) {
            if (p.getIdPedido().equals(idPedido)) {
                pedidoEncontrado = p;
                break;
            }
        }

        // Si no esta vigente, busca en el historial
        if (pedidoEncontrado == null) {
            for (Pedido p : pedidosTerminados) {
                if (p.getIdPedido().equals(idPedido)) {
                    pedidoEncontrado = p;
                    break;
                }
            }
        }

        if (pedidoEncontrado == null) {
            System.out.println("No se encontro el pedido N°: " + idPedido);
            return;
        }

        // Solo se puede rastrear si el pedido implementa Rastreable
        if (pedidoEncontrado instanceof Rastreable) {
            ((Rastreable) pedidoEncontrado).verHistorial();
        } else {
            System.out.println("Este tipo de pedido no se puede buscar.");
        }
    }

}