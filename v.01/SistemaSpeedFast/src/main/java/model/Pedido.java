package model;

public class Pedido {
    // Atributos
    private String idPedido;
    private String cliente;
    private String direccion;
    private String tipoPedido;

    // Constructor
    public Pedido(String idPedido, String cliente, String direccion) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.direccion = direccion;
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
    public String getTipoPedido() {
        return tipoPedido;
    }
    public void setTipoPedido(String tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    // Metodo Asignar Repartidor (version generica, sera sobreescrito por las subclases)
    public void asignarRepartidor() {
        System.out.println("Pedido " + getIdPedido() + " - Buscando un repartidor disponible.");
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido " + getIdPedido() + " - Repartidor " + nombreRepartidor + " asignado.");
    }

}