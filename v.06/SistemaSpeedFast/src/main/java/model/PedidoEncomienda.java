package model;

import interfaces.Cancelable;
import interfaces.Rastreable;

public class PedidoEncomienda extends Pedido implements Cancelable, Rastreable {
    // Atributos
    private double peso;
    private double volumen;

    // Constructor
    public PedidoEncomienda(String idPedido, String cliente, String direccion, double distanciaKm, double peso, double volumen) {
        super(idPedido, cliente, direccion, distanciaKm);
        this.peso = peso;
        this.volumen = volumen;
        setTipoPedido("Encomienda");
    }

    // Metodo Getter and Setter
    public double getPeso() {
        return peso;
    }
    public void setPeso(double peso) {
        this.peso = peso;
    }
    public double getVolumen() {
        return volumen;
    }
    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }

    // Metodo Mostrar Resumen Pedido
    @Override
    public String mostrarResumen() {
        return super.mostrarResumen() + "\n" +
                "Peso: " + getPeso() + " kg\n" +
                "Volumen: " + getVolumen() + " m3";
    }

    // Metodo para calcular el tiempo de entrega: 20 min base + 1.5 min por km
    @Override
    public double calcularTiempoEntrega() {
        double tiempo = 20 + (1.5 * getDistanciaKm());
        return Math.round(tiempo);
    }

    // Metodo Asignar Repartidor (sobreescritura)
    @Override
    public String asignarRepartidor() {
        // Condicion: peso >= 100 kg
        if (peso >= 100) {
            return "Pedido " + getIdPedido() + " - Encomienda: Peso validado: " +
                    getPeso() + " kg. Buscando repartidor para carga.";
        } else {
            return "Pedido " + getIdPedido() + " - Encomienda: Buscando repartidor disponible.";
        }
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public String asignarRepartidor(String nombreRepartidor) {
        // Condicion
        if (peso >= 100) {
            return "Pedido N°: " + getIdPedido() + " - Repartidor " + nombreRepartidor +
                    " asignado. Encomienda de " + getPeso() + " kg, requiere transporte especial.";
        } else {
            return "Pedido N°: " + getIdPedido() + ": Repartidor " + nombreRepartidor +
                    " asignado. Encomienda peso normal.";
        }
    }

    // Metodo Cancelar (interfaz Cancelable)
    @Override
    public String cancelar() {
        return "Pedido N°: " + getIdPedido() + " - Encomienda de " + getPeso() +
                " kg ha sido CANCELADA. Se notificará al cliente " + getCliente() + ".";
    }

    // Metodo Ver Historial (interfaz Rastreable)
    @Override
    public String verHistorial() {
        return "Pedido N°: " + getIdPedido() + " | Cliente: " + getCliente() +
                " | Peso: " + getPeso() + " kg | Estado actual: En Camino.";
    }

}