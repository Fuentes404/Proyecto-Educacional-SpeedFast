package model;

import interfaces.Cancelable;
import interfaces.Rastreable;

public class PedidoComida extends Pedido implements Cancelable, Rastreable {
    // Atributos
    private String restaurante;
    private String tiempoPreparacion;

    // Constructor
    public PedidoComida(String idPedido, String cliente, String direccion, double distanciaKm, String restaurante, String tiempoPreparacion) {
        super(idPedido, cliente, direccion, distanciaKm);
        this.restaurante = restaurante;
        this.tiempoPreparacion = tiempoPreparacion;
        setTipoPedido("Comida");
    }

    // Metodos Getter and Setter
    public String getRestaurante() {
        return restaurante;
    }
    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }
    public String getTiempoPreparacion() {
        return tiempoPreparacion;
    }
    public void setTiempoPreparacion(String tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    // Metodo para calcular el tiempo de entrega: 15 min base + 2 min por km
    @Override
    public double calcularTiempoEntrega() {
        double tiempo = 15 + (2 * getDistanciaKm());
        double resultado = Math.round(tiempo);
        System.out.println("Tiempo Estimado: " + resultado + " min.");
        return resultado;
    }

    // Metodo Asignar Repartidor (sobreescritura)
    @Override
    public void asignarRepartidor() {
        System.out.println("Pedido N°: " + getIdPedido() + " - Comida: " + getRestaurante() +
                ", Buscando repartidor con MOCHILA TERMICA, Tiempo de preparacion: " + getTiempoPreparacion());
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido " + getIdPedido() + " - Repartidor " + nombreRepartidor +
                " asignado, repartidor con MOCHILA TERMICA para pedido de " + getRestaurante());
    }

    // Metodo Cancelar (interfaz Cancelable)
    @Override
    public void cancelar() {
        System.out.println("Pedido N°: " + getIdPedido() + " - Comida de " + getRestaurante() +
                " ha sido CANCELADO. Se notificará al cliente: " + getCliente() + ".");
    }

    // Metodo Ver Historial (interfaz Rastreable)
    @Override
    public void verHistorial() {
        System.out.println("Pedido N°: " + getIdPedido() + " | Cliente: " + getCliente() +
                " | Restaurante: " + getRestaurante() + " | Estado actual: En Camino.");
    }
}