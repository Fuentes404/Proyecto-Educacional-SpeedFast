package model;

public abstract class Pedido {
    // Atributos
    private String idPedido;
    private String cliente;
    private String direccion;
    private double distanciaKm;
    private String tipoPedido;

    // Constructor
    public Pedido(String idPedido, String cliente, String direccion, double distanciaKm) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.direccion = direccion;
        this.distanciaKm = distanciaKm;
        this.tipoPedido = "General";
    }

    // Metodos Getter and Setter
    public String getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }
    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public double getDistanciaKm() {
        return distanciaKm;
    }
    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
    public String getTipoPedido() {
        return tipoPedido;
    }
    public void setTipoPedido(String tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    // Metodo Mostrar Resumen Pedido
    public String mostrarResumen() {
        return "Datos del Pedido: " + getTipoPedido() + "\n" +
                "Pedido N°: " + getIdPedido() + "\n" +
                "Cliente: " + getCliente() + "\n" +
                "Direccion: " + getDireccion() + "\n" +
                "Distancia: " + getDistanciaKm() + " km";
    }

    // Metodo abstracto para calcular el tiempo de entrega
    public abstract double calcularTiempoEntrega();

    // Metodo abstracto para Asignar Repartidor
    public abstract String asignarRepartidor();

    // Metodo abstracto Asignar Repartidor con nombre (sobrecarga)
    public abstract String asignarRepartidor(String nombreRepartidor);

}