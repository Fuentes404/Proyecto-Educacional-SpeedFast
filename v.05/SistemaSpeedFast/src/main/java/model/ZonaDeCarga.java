package model;

import java.util.LinkedList;
import java.util.Queue;

public class ZonaDeCarga {
    // Atributos
    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();
    private boolean cerrada = false;

    // Constructor agrega un pedido a la cola
    public synchronized void agregarPedido(Pedido p) {
        pedidosPendientes.add(p);
        System.out.println("Pedido registrado: " + p.getId() + " agregado a la Zona de Carga.");
        notifyAll();
    }

    public synchronized Pedido retirarPedido() {
        while (pedidosPendientes.isEmpty() && !cerrada) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return pedidosPendientes.poll();
    }

    public synchronized void cerrar() {
        this.cerrada = true;
        notifyAll();
    }
}