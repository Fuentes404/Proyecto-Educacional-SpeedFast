package services;

import dao.RepartidorDAO;
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
public class ControladorRepartidores {

    // Atributos

    // Linea separadora para los mensajes de salida
    private static final String SEPARADOR = "---------------------------------------------";

    // Largo maximo del nombre (VARCHAR(100) en la tabla)
    private static final int LARGO_MAX_NOMBRE = 100;

    // Acceso a la base de datos
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // Consultas

    // Devuelve: los repartidores registrados en la base de datos
    public List<Repartidor> getRepartidores() {
        return repartidorDAO.listarTodos();
    }

    // Devuelve: los nombres de los repartidores, para llenar el combo de la interfaz
    public List<String> getNombres() {
        List<String> nombres = new ArrayList<>();
        for (Repartidor r : repartidorDAO.listarTodos()) {
            nombres.add(r.getNombre());
        }
        return nombres;
    }

    // Indica si hay al menos un repartidor registrado
    public boolean hayRepartidores() {
        return !repartidorDAO.listarTodos().isEmpty();
    }

    // Registro y eliminacion

    // Registra un repartidor nuevo.
    // Devuelve: el repartidor creado, con el ID que genero MySQL
    public Repartidor registrar(String nombre) {
        String limpio = (nombre == null) ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El nombre del repartidor es obligatorio.");
        }
        if (limpio.length() > LARGO_MAX_NOMBRE) {
            throw new IllegalArgumentException("El nombre no puede superar los " + LARGO_MAX_NOMBRE + " caracteres.");
        }
        if (repartidorDAO.buscarPorNombre(limpio) != null) {
            throw new IllegalArgumentException("Ya existe un repartidor llamado " + limpio + ".");
        }

        Repartidor nuevo = new Repartidor(limpio);
        if (!repartidorDAO.guardar(nuevo)) {
            throw new IllegalStateException("No se pudo guardar el repartidor.");
        }
        return nuevo;
    }

    // Elimina un repartidor, salvo que tenga entregas registradas
    public void eliminar(Repartidor repartidor) {
        int entregas = repartidorDAO.contarEntregas(repartidor.getId());
        if (entregas > 0) {
            throw new IllegalStateException("No se puede eliminar a " + repartidor.getNombre()
                    + ": tiene " + entregas + " entrega(s) registrada(s).");
        }
        if (!repartidorDAO.eliminar(repartidor.getId())) {
            throw new IllegalStateException("No se pudo eliminar al repartidor " + repartidor.getNombre() + ".");
        }
    }

    // Simulacion

    // Simula las entregas con un hilo por repartidor y espera a que todos terminen.
    // Los mensajes se envian al consumidor "salida".
    // Devuelve: true si todos terminaron bien; false si no habia datos, hubo timeout o interrupcion
    public boolean simularEntregas(List<Pedido> pedidos, Map<Integer, String> asignaciones, Consumer<String> salida) {
        List<Repartidor> registrados = repartidorDAO.listarTodos();
        if (pedidos.isEmpty() || registrados.isEmpty()) {
            salida.accept("No hay pedidos o repartidores para simular.");
            return false;
        }

        // 1. Copias de los repartidores con listas vacias (asi no se acumulan pedidos entre simulaciones)
        List<Repartidor> ruta = new ArrayList<>();
        for (Repartidor r : registrados) {
            ruta.add(new Repartidor(r.getId(), r.getNombre(), new ArrayList<>(), salida));
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