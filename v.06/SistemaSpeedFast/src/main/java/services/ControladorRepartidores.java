package services;

import model.Pedido;
import model.Repartidor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// Controlador de repartidores.
// Mantiene la lista de repartidores y coordina la simulacion de entregas concurrentes.
public class ControladorRepartidores {

    // Atributos

    // Linea separadora para los mensajes de salida
    private static final String SEPARADOR = "---------------------------------------------";

    // Repartidores registrados
    private final List<Repartidor> repartidores = new ArrayList<>();

    // Constructor
    // Deja registrados tres repartidores de ejemplo
    public ControladorRepartidores() {
        repartidores.add(new Repartidor("Carlos", new ArrayList<>()));
        repartidores.add(new Repartidor("Fernanda", new ArrayList<>()));
        repartidores.add(new Repartidor("Matias", new ArrayList<>()));
    }

    // Consultas

    // Devuelve: los nombres de los repartidores, para llenar el combo de la interfaz
    public List<String> getNombres() {
        List<String> nombres = new ArrayList<>();
        for (Repartidor r : repartidores) {
            nombres.add(r.getNombre());
        }
        return nombres;
    }

    // Indica si hay al menos un repartidor registrado
    public boolean hayRepartidores() {
        return !repartidores.isEmpty();
    }

    // Simulacion

    // Simula las entregas con un hilo por repartidor y espera a que todos terminen.
    // Los mensajes se envian al consumidor "salida".
    // Devuelve: true si todos terminaron bien; false si no habia datos, hubo timeout o interrupcion
    public boolean simularEntregas(List<Pedido> pedidos, Map<String, String> asignaciones, Consumer<String> salida) {
        if (pedidos.isEmpty() || repartidores.isEmpty()) {
            salida.accept("No hay pedidos o repartidores para simular.");
            return false;
        }

        // 1. Copias de los repartidores con listas vacias (asi no se acumulan pedidos entre simulaciones)
        List<Repartidor> ruta = new ArrayList<>();
        for (Repartidor r : repartidores) {
            ruta.add(new Repartidor(r.getNombre(), new ArrayList<>(), salida));
        }

        // 2. Respetar las asignaciones manuales
        List<Pedido> sinAsignar = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            Repartidor elegido = buscarEnRuta(ruta, asignaciones.get(pedido.getIdPedido()));
            if (elegido != null) {
                elegido.getPedidosAsignados().add(pedido);
            } else {
                sinAsignar.add(pedido);
            }
        }

        // 3. Repartir el resto entre quien tenga menos pedidos
        for (Pedido pedido : sinAsignar) {
            Repartidor menosCargado = ruta.get(0);
            for (Repartidor r : ruta) {
                if (r.getPedidosAsignados().size() < menosCargado.getPedidosAsignados().size()) {
                    menosCargado = r;
                }
            }
            menosCargado.getPedidosAsignados().add(pedido);
        }

        // 4. Solo salen a ruta los repartidores que tienen pedidos
        List<Repartidor> activos = new ArrayList<>();
        for (Repartidor r : ruta) {
            if (!r.getPedidosAsignados().isEmpty()) {
                activos.add(r);
            }
        }

        // 5. Pool de hilos: uno por repartidor activo
        ExecutorService executor = Executors.newFixedThreadPool(activos.size());

        salida.accept("Iniciando simulacion de entregas concurrentes: ");
        salida.accept(SEPARADOR);

        for (Repartidor repartidor : activos) {
            executor.execute(repartidor);
        }

        // 6. shutdown(): no acepta tareas nuevas, pero deja terminar las que ya corren
        executor.shutdown();
        int maxPedidosPorRepartidor = 0;
        for (Repartidor r : activos) {
            maxPedidosPorRepartidor = Math.max(maxPedidosPorRepartidor, r.getPedidosAsignados().size());
        }

        // Tiempo maximo de espera: cada entrega tarda hasta 3 s, mas 5 s de margen
        long timeoutSegundos = maxPedidosPorRepartidor * 3L + 5;

        // 7. awaitTermination: espera a que terminen todos los hilos o se cumpla el timeout
        try {
            if (!executor.awaitTermination(timeoutSegundos, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                salida.accept("La simulacion excedio el tiempo maximo y fue detenida.");
                return false;
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            salida.accept("La simulacion fue interrumpida.");
            return false;
        }

        salida.accept(SEPARADOR);
        salida.accept("Simulacion finalizada: Todos los repartidores completaron sus rutas.");
        return true;
    }

    // Busca en la ruta al repartidor con ese nombre (ignora mayusculas).
    // Devuelve: el repartidor, o null si el nombre es null o no existe
    private Repartidor buscarEnRuta(List<Repartidor> ruta, String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Repartidor r : ruta) {
            if (r.getNombre().equalsIgnoreCase(nombre)) {
                return r;
            }
        }
        return null;
    }
}