package model;

// - Función: Clase base de los pedidos, guarda los datos que comparten todos los tipos
public abstract class Pedido {

    // — Atributos
    private int idPedido;
    private String cliente;
    private String direccion;
    private double distanciaKm;
    private TipoPedido tipoPedido;
    private EstadoPedido estado;

    // — Constructor
    public Pedido(int idPedido, String cliente, String direccion, double distanciaKm) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.direccion = direccion;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
    }

    // — Métodos Getter and Setter
    public int getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(int idPedido) {
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
    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }
    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = tipoPedido;
    }
    public EstadoPedido getEstado() {
        return estado;
    }
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    // — Métodos Funcionales

    // Metodo Mostrar Resumen Pedido
    public String mostrarResumen() {
        return "Datos del Pedido: " + getTipoPedido() + "\n" +
                "Pedido N°: " + getIdPedido() + "\n" +
                "Cliente: " + getCliente() + "\n" +
                "Direccion: " + getDireccion() + "\n" +
                "Distancia: " + getDistanciaKm() + " km\n" +
                "Estado: " + getEstado();
    }

    // -- Métodos abstractos (cada tipo de pedido los desarrolla)
    // Metodo abstracto para calcular el tiempo de entrega
    public abstract double calcularTiempoEntrega();

    // Metodo abstracto para Asignar Repartidor
    public abstract String asignarRepartidor();

    // Metodo abstracto Asignar Repartidor con nombre (sobrecarga)
    public abstract String asignarRepartidor(String nombreRepartidor);

}