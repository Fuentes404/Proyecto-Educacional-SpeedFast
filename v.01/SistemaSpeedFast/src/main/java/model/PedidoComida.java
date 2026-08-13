package model;

public class PedidoComida extends Pedido {
    // Atributos
    private String restaurante;
    private String tiempoPreparacion;

    // Constructor
    public PedidoComida(String idPedido, String cliente, String direccion,
                        String restaurante, String tiempoPreparacion) {
        super(idPedido, cliente, direccion);
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

    // Metodo Asignar Repartidor (sobreescritura: requiere mochila termica)
    @Override
    public void asignarRepartidor() {
        System.out.println("Pedido " + getIdPedido() + ": (Comida) " + restaurante + ", Buscando repartidor con MOCHILA TERMICA, Tiempo de preparacion: " + tiempoPreparacion);
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido " + getIdPedido() + ": Repartidor " + nombreRepartidor +
                " asignado, repartidor con MOCHILA TERMICA para pedido de " + restaurante);
    }
}