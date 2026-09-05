package ui;

import services.ControladorEnvios;
import services.Repartidor;
import model.Pedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Scanner para leer lo que el usuario escribe por consola
        Scanner sc = new Scanner(System.in);

        // Controlador que administra el ArrayList de pedidos
        ControladorEnvios controlador = new ControladorEnvios();

        // Bucle Plincipal
        // Se repite mientras el usuario no elija la opción 0 Salir
        int opcion;
        do {
            // Menu Principal
            System.out.println("\n------ MENÚ SpeedFast ------");
            System.out.println("1. Crear Pedido");
            System.out.println("2. Cancelar Pedido");
            System.out.println("3. Despachar Pedido");
            System.out.println("4. Ver Pedidos Vigentes");
            System.out.println("5. Ver Historial de Pedidos");
            System.out.println("6. Buscar Pedido");
            System.out.println("7. Simular Entregas Concurrentes");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            // Leemos la línea y la convertimos a número entero
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1: {
                    // Submenú Crear Pedido
                    int subOpcion;
                    do {
                        System.out.println("\n------ MENÚ Crear Pedido ------");
                        System.out.println("Indique el tipo de pedido a crear: ");
                        System.out.println("1. Pedido de Comida");
                        System.out.println("2. Encomienda");
                        System.out.println("3. Pedido Express");
                        System.out.println("0. volver");

                        // Leemos la linea y la convertimos a numero entero
                        subOpcion = Integer.parseInt(sc.nextLine());

                        switch (subOpcion) {
                            case 1: {

                                System.out.print("Cliente: ");
                                String cliente = sc.nextLine();
                                System.out.print("Dirección: ");
                                String direccion = sc.nextLine();
                                System.out.print("Distancia (km): ");
                                double distancia = Double.parseDouble(sc.nextLine());
                                System.out.print("Restaurante: ");
                                String restaurante = sc.nextLine();
                                System.out.print("Tiempo de preparación ej: 20 min ");
                                String tiempoPreparacion = sc.nextLine();

                                // Le entregamos la informacion al controlador
                                controlador.crearPedido(cliente, direccion, distancia, restaurante, tiempoPreparacion);

                                break;
                            }

                            case 2: {

                                System.out.print("Cliente: ");
                                String cliente = sc.nextLine();
                                System.out.print("Dirección: ");
                                String direccion = sc.nextLine();
                                System.out.print("Distancia (km): ");
                                double distancia = Double.parseDouble(sc.nextLine());
                                System.out.print("Peso (kg): ");
                                double peso = Double.parseDouble(sc.nextLine());
                                System.out.print("Volumen (m3): ");
                                double volumen = Double.parseDouble(sc.nextLine());

                                // Le entregamos la informacion al controlador
                                controlador.crearPedido(cliente, direccion, distancia, peso, volumen);

                                break;
                            }

                            case 3: {

                                System.out.print("Cliente: ");
                                String cliente = sc.nextLine();
                                System.out.print("Dirección: ");
                                String direccion = sc.nextLine();
                                System.out.print("Distancia (km): ");
                                double distancia = Double.parseDouble(sc.nextLine());
                                System.out.print("Tienda: ");
                                String tienda = sc.nextLine();

                                // Le entregamos la informacion al controlador
                                controlador.crearPedido(cliente, direccion, distancia, tienda);

                                break;
                            }

                            case 0:
                                System.out.println("");
                                break;

                            default:
                                System.out.println("Opción inválida");
                        }


                    } while (subOpcion != 0);
                    break;
                }

                // Menu Cancelar Pedidos Vigentes
                case 2: {

                    String subOpcionCancelar;
                    do {
                        System.out.println("\n------ MENÚ Cancelar Pedidos ------");
                        System.out.println("Pedidos que se pueden cancelar: ");
                        controlador.verCancelables();
                        System.out.println("Ingrese el N° Pedido que desea Cancelar: ");
                        System.out.println("0. volver");
                        System.out.println("");

                        subOpcionCancelar = sc.nextLine();

                        if (subOpcionCancelar.equals("0")) {
                            // Volver al menú principal
                        } else {
                            controlador.cancelarPedido(subOpcionCancelar);
                        }

                    } while (!subOpcionCancelar.equals("0"));
                    break;
                }

                // Menu Despacho pedido
                case 3: {

                    String subOpcionDespachoPedido;
                    do {
                        System.out.println("\n------ MENÚ Despacho Pedido ------");
                        System.out.println("Pedidos a designar: ");
                        controlador.verDespachables();
                        System.out.println("Ingrese el N° Pedido que desea Despachar: ");
                        System.out.println("0. volver");

                        subOpcionDespachoPedido = sc.nextLine();

                        if (subOpcionDespachoPedido.equals("0")) {
                            // El usuario eligió volver, no se hace nada
                        } else {
                            controlador.despacharPedido(subOpcionDespachoPedido);
                        }

                    } while (!subOpcionDespachoPedido.equals("0"));
                    break;
                }
                // Menu Ver Pedidos Vigentes
                case 4: {

                    int subOpcionVerPedidosVigentes;
                    do {
                        System.out.println("\n------ MENÚ Ver Pedidos Vigentes ------");
                        controlador.verVigentes();
                        System.out.println("0. volver");

                        // Leemos la linea y la convertimos a numero entero
                        subOpcionVerPedidosVigentes = Integer.parseInt(sc.nextLine());

                        switch (subOpcionVerPedidosVigentes) {
                            case 0:
                                // Volver al menú principal
                                break;
                            default:
                                System.out.println("Opción inválida");
                        }

                    } while (subOpcionVerPedidosVigentes != 0);
                    break;
                }

                case 5: {

                    int subOpcionVerHistorial;
                    do {
                        System.out.println("\n------ MENÚ Ver Historial de Pedidos ------");
                        controlador.verPedidosTerminados();
                        System.out.println("0. volver");

                        // Leemos la linea y la convertimos a numero entero
                        subOpcionVerHistorial = Integer.parseInt(sc.nextLine());

                        switch (subOpcionVerHistorial) {
                            case 0:
                                break;
                            default:
                                System.out.println("Opcion invalida");
                        }

                    } while (subOpcionVerHistorial != 0);
                    break;
                }

                // Menu Buscar Pedido
                case 6: {

                    System.out.println("\n------ MENÚ Buscar Pedido ------");
                    System.out.print("Ingrese el N° de Pedido a buscar: ");
                    String idBusqueda = sc.nextLine();
                    controlador.buscarPedido(idBusqueda);
                    break;
                }

                // Menu Simular Entregas Concurrentes
                case 7: {

                    // Obtenemos la lista de pedidos vigentes creados por el usuario
                    ArrayList<Pedido> pedidosVigentes = controlador.getPedidosVigentes();

                    // Si no hay pedidos vigentes, se informa y no se ejecuta la simulacion
                    if (pedidosVigentes.isEmpty()) {
                        System.out.println("No se encuentran pedidos en curso..... Esperando");
                        break;
                    }

                    // Lista fija de Repartidores
                    List<Repartidor> repartidores = new ArrayList<>();
                    repartidores.add(new Repartidor("Carlos", new ArrayList<>()));
                    repartidores.add(new Repartidor("Fernanda", new ArrayList<>()));
                    repartidores.add(new Repartidor("Matias", new ArrayList<>()));

                    // Recorrido de los pedidos vigentes para repartirlos entre los repartidores disponibles (round-robin)
                    for (int i = 0; i < pedidosVigentes.size(); i++) {
                        int indiceRepartidor = i % repartidores.size();
                        repartidores.get(indiceRepartidor).getPedidosAsignados().add(pedidosVigentes.get(i));
                    }

                    // Creacion del ExecutorService con un pool de hilos igual a la cantidad de Repartidores
                    ExecutorService executor = Executors.newFixedThreadPool(repartidores.size());

                    System.out.println("\nIniciando simulacion de entregas concurrentes...");

                    // Envio de cada repartidor al pool para que se ejecute en paralelo
                    for (Repartidor repartidor : repartidores) {
                        executor.execute(repartidor);
                    }

                    // Cierre del executor
                    executor.shutdown();

                    // Espera hasta 10 segundos a que los repartidores terminen sus entregas antes de continuar
                    try {
                        executor.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        System.out.println("La simulacion fue interrumpida.");
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("\nSimulacion finalizada - Todos los repartidores completaron sus rutas.");

                    break;
                }

                default:
                    System.out.println("Opción inválida");
            }

        } while (opcion != 0);

    }

}