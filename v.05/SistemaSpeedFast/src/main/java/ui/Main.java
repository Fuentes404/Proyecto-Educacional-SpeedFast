package ui;

import model.Pedido;
import model.EstadoPedido;
import model.Repartidor;
import model.ZonaDeCarga;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Se crea la zona de carga compartida entre el hilo principal y los repartidores
        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();

        System.out.println("--- REGISTRANDO PEDIDOS ---");
        // Se registran los pedidos iniciales
        zonaDeCarga.agregarPedido(new Pedido(1001, "Calle Falsa 123", EstadoPedido.PENDIENTE));
        zonaDeCarga.agregarPedido(new Pedido(1002, "Calle verdadera 456", EstadoPedido.PENDIENTE));
        zonaDeCarga.agregarPedido(new Pedido(1003, "Fonda de la esquina 789", EstadoPedido.PENDIENTE));
        zonaDeCarga.agregarPedido(new Pedido(1004, "Anticucho a 1000", EstadoPedido.PENDIENTE));
        zonaDeCarga.agregarPedido(new Pedido(1005, "Las chacras 200", EstadoPedido.PENDIENTE));

        // Se marca la zona de carga como cerrada no se agregarán más pedidos.
        zonaDeCarga.cerrar();

        System.out.println("\n--- INICIANDO REPARTIDORES ---");

        // Pool de 3 hilos, uno por cada repartidor
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Cada Repartidor es un Runnable que consume pedidos de la zona de carga
        executor.execute(new Repartidor("Repartidor Pablo", zonaDeCarga));
        executor.execute(new Repartidor("Repartidor Lourdes", zonaDeCarga));
        executor.execute(new Repartidor("Repartidor Sebastian", zonaDeCarga));

        // No se aceptan más tareas
        executor.shutdown();

        try {
            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("\nTodos los pedidos han sido entregados correctamente");
            } else {
                System.out.println("\nSe agotó el tiempo de espera antes de que terminaran todos los repartidores");
            }
        } catch (InterruptedException e) {
            // Se restaura el estado de interrupción del hilo principal
            Thread.currentThread().interrupt();
        }
    }
}