package services;

import model.Pedido;
import java.util.List;
import java.util.Random;

public class Repartidor implements Runnable {

    // Atributos
    private String nombre;
    private List<Pedido> pedidosAsignados;
    private Random random = new Random();

    // Constructor
    public Repartidor(String nombre, List<Pedido> pedidosAsignados) {
        this.nombre = nombre;
        this.pedidosAsignados = pedidosAsignados;
    }

    // Metodo Getter
    public String getNombre() {
        return nombre;
    }

    // Metodo Getter de la lista de pedidos asignados
    public List<Pedido> getPedidosAsignados() {
        return pedidosAsignados;
    }

    // Metodo run
    // Herencia de la clase Thread
    @Override
    public void run() {
        // Mensaje de inicio de ruta del repartidor
        System.out.println("Repartidor " + nombre + " inicia su ruta con " +
                pedidosAsignados.size() + " pedido(s).");

        for (Pedido pedido : pedidosAsignados) {

            // Mensaje de inicio de entrega del pedido
            System.out.println("[" + nombre + "] Iniciando entrega del Pedido N°: " +
                    pedido.getIdPedido() + " (" + pedido.getTipoPedido() + ")");

            // Simulacion de la entrega
            try {
                // Genera un tiempo aleatorio entre 1 y 3 segundos (1000 a 3000 ms)
                int tiempoSimulado = 1000 + random.nextInt(2000);
                Thread.sleep(tiempoSimulado);
            } catch (InterruptedException e) {
                // Si el hilo es interrumpido durante la espera, se informa y se corta la ejecucion
                System.out.println("[" + nombre + "] Entrega interrumpida");
                Thread.currentThread().interrupt();
            }

            // Mensaje pedido entregado con exito
            System.out.println("[" + nombre + "] Pedido N°: " + pedido.getIdPedido() +
                    " entregado con éxito");
        }

        // Mensaje final: el repartidor termino todas sus entregas
        System.out.println("Repartidor: " + nombre + " ha finalizado todas sus entregas");
    }
}