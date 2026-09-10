package model;

public class Pedido {
    // Atributos
    protected int id;
    protected String direccionEntrega;
    protected EstadoPedido estado;

    // Constructor
    public Pedido(int id, String direccionEntrega, EstadoPedido estado) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
    }

    // Metodos Getter and setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDireccionEntrega() {
        return direccionEntrega;
    }
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }
    public EstadoPedido getEstado() {
        return estado;
    }
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    // Metodo funcional define el estado
    public void setEstado(String nuevoEstado) {
        this.estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
    }

    // Metodo toString
    @Override
    public String toString() {
        return "Pedido{" + "id=" + id + ", direccionEntrega='" + direccionEntrega + '\'' + ", estado=" + estado + '}';
    }
}