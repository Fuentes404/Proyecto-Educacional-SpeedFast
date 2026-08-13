package model;

public class PedidoEncomienda extends Pedido {
    // Atributos
    private double peso;
    private double volumen;

    // Constructor
    public PedidoEncomienda(String idPedido, String cliente, String direccion,
                            double peso, double volumen) {
        super(idPedido, cliente, direccion);
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

    // Metodo Asignar Repartidor (sobreescritura: valida solo el peso)
    @Override
    public void asignarRepartidor() {
        // Condicion: peso >= 100 k
        if (peso >= 100) {
            System.out.println("Pedido " + getIdPedido() + " (Encomienda): Peso validado: " +
                    peso + " kg. Buscando repartidor especial para carga.");
        } else {
            System.out.println("Pedido " + getIdPedido() + " (Encomienda): Buscando repartidor disponible.");
        }
    }

    // Metodo Asignar Repartidor con nombre (sobrecarga)
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        // Condicion
        if (peso >= 100) {
            System.out.println("Pedido " + getIdPedido() + ": Repartidor " + nombreRepartidor +
                    " asignado. Encomienda de " + peso + " kg, requiere transporte especial.");
        } else {
            System.out.println("Pedido " + getIdPedido() + ": Repartidor " + nombreRepartidor +
                    " asignado. Encomienda dentro de parametros normales.");
        }
    }

}