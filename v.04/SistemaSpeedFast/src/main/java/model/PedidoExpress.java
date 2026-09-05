package model;

import interfaces.Despachable;

public class PedidoExpress extends Pedido implements Despachable {
    // Atributos
    private String tienda;

    // Constructor
    public PedidoExpress(String idPedido, String cliente, String direccion, double distanciaKm, String tienda) {
        super(idPedido, cliente, direccion, distanciaKm);
        this.tienda = tienda;
        setTipoPedido("Compra Express");
    }

    // Metodo Getter and Setter
    public String getTienda() {
        return tienda;
    }
    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    // Metodo para calcular el tiempo de entrega: 10 min base +5 si distancia > 5 km-+
    @Override
    public double calcularTiempoEntrega() {
        double tiempo = 10;
        if (getDistanciaKm() > 5) {
            tiempo += 5;
        }
        System.out.println("Tiempo Estimado: " + tiempo + " min.");
        return tiempo;
    }

    // Metodo Asignar Repartidor (sobreescritura)
    @Override
    public void asignarRepartidor() {
        System.out.println("Pedido " + getIdPedido() + " - Express: " + getTienda() +
                ", Buscando el repartidor MAS CERCANO " + getDistanciaKm() + " km.");
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido " + getIdPedido() + " - Repartidor " + nombreRepartidor +
                " asignado " + getDistanciaKm() + " km de la tienda " + getTienda());
    }

    // Metodo Despachar (interfaz Despachable)
    @Override
    public void despachar() {
        System.out.println("Pedido N°: " + getIdPedido() + " - Compra Express de " + getTienda() +
                " DESPACHADA hacia " + getDireccion() + ". Entrega en curso.");
    }

}