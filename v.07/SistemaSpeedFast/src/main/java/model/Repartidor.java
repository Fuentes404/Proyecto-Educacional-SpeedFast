package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Repartidor implements Runnable {

    // Atributos
    private int id;
    private String nombre;
    private List<Pedido> pedidosAsignados;
    private Consumer<String> salida;
    private Random random = new Random();

    // Constructor para un repartidor nuevo: el ID lo asigna MySQL al guardarlo
    public Repartidor(String nombre) {
        this(0, nombre, new ArrayList<>());
    }

    // Constructor: los mensajes salen por consola
    public Repartidor(int id, String nombre, List<Pedido> pedidosAsignados) {
        this(id, nombre, pedidosAsignados, System.out::println);
    }

    // Constructor: los mensajes salen por el JTextArea
    public Repartidor(int id, String nombre, List<Pedido> pedidosAsignados, Consumer<String> salida) {
        this.id = id;
        this.nombre = nombre;
        this.pedidosAsignados = pedidosAsignados;
        this.salida = salida;
    }

    // Metodos Getter and Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
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