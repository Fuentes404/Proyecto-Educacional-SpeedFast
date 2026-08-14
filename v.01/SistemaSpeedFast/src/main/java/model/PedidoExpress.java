package model;

public class PedidoExpress extends Pedido {
    // Atributos
    private String tienda;
    private int distancia;

    // Constructor
    public PedidoExpress(String idPedido, String cliente, String direccion,
                         String tienda, int distancia) {
        super(idPedido, cliente, direccion);
        this.tienda = tienda;
        this.distancia = distancia;
        setTipoPedido("Compra Express");
    }

    // Metodo Getter and Setter
    public String getTienda() {
        return tienda;
    }
    public void setTienda(String tienda) {
        this.tienda = tienda;
    }
    public int getDistancia() {
        return distancia;
    }
    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    // Metodo Asignar Repartidor (sobreescritura: repartidor mas cercano con disponibilidad inmediata)
    @Override
    public void asignarRepartidor() {
        System.out.println("Pedido " + getIdPedido() + " - Express: " + getTienda() +
                ", Buscando el repartidor MAS CERCANO " + getDistancia() + " km.");
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Pedido " + getIdPedido() + " - Repartidor " + nombreRepartidor +
                " asignado " + getDistancia() + " km de la tienda " + getTienda());
    }

}