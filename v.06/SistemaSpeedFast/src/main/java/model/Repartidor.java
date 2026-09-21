package model;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Repartidor implements Runnable {

    // Atributos
    private String nombre;
    private List<Pedido> pedidosAsignados;
    private Consumer<String> salida;   // Donde se envian los mensajes (la GUI o la consola)
    private Random random = new Random();

    // Constructor: los mensajes salen por consola
    public Repartidor(String nombre, List<Pedido> pedidosAsignados) {
        this(nombre, pedidosAsignados, System.out::println);
    }

    // Constructor: los mensajes salen por el consumidor indicado (ej: el JTextArea)
    public Repartidor(String nombre, List<Pedido> pedidosAsignados, Consumer<String> salida) {
        this.nombre = nombre;
        this.pedidosAsignados = pedidosAsignados;
        this.salida = salida;
    }

    // Metodos Getter
    public String getNombre() {
        return nombre;
    }

    public List<Pedido> getPedidosAsignados() {
        return pedidosAsignados;
    }

    // Metodo run (implementacion de Runnable)
    @Override
    public void run() {
        // Mensaje de inicio de ruta del repartidor
        salida.accept("Repartidor " + nombre + " inicia su ruta con " +
                pedidosAsignados.size() + " pedido(s).");

        for (Pedido pedido : pedidosAsignados) {

            // Mensaje de inicio de entrega del pedido
            salida.accept("[" + nombre + "] Iniciando entrega del Pedido N°: " +
                    pedido.getIdPedido() + " (" + pedido.getTipoPedido() + ")");

            // Simulacion de la entrega
            try {
                // Genera un tiempo aleatorio entre 1 y 3 segundos (1000 a 3000 ms)
                int tiempoSimulado = 1000 + random.nextInt(2000);
                Thread.sleep(tiempoSimulado);
            } catch (InterruptedException e) {
                // Si el hilo es interrumpido durante la espera, se informa y se corta la ejecucion
                salida.accept("[" + nombre + "] Entrega interrumpida");
                Thread.currentThread().interrupt();
                return;
            }

            // Mensaje pedido entregado con exito
            salida.accept("[" + nombre + "] Pedido N°: " + pedido.getIdPedido() +
                    " entregado con éxito");
        }

        // Mensaje final
        salida.accept("Repartidor: " + nombre + " ha finalizado todas sus entregas");
    }
}