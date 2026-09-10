package model;

public class Repartidor implements Runnable {
    // Atributos
    private String nombre;
    private ZonaDeCarga zonaDeCarga;

    // Constructor
    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    // Metodos que implementa Runnable
    @Override
    public void run() {
        try {
            Pedido pedido;
            // Procesa pedidos mientras existan en la zona de carga
            while ((pedido = zonaDeCarga.retirarPedido()) != null) {
                pedido.setEstado(EstadoPedido.EN_REPARTO);
                System.out.println("[" + nombre + "] Tomó el pedido: " + pedido.getId() + " - Estado: " + pedido.getEstado());

                // Tiempo de reaccion
                Thread.sleep(1000);

                // Tras entregar el pedido
                pedido.setEstado(EstadoPedido.ENTREGADO);
                System.out.println("[" + nombre + "] Entregó el pedido: " + pedido.getId() + " en: " + pedido.getDireccionEntrega() + " - Estado: " + pedido.getEstado());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}