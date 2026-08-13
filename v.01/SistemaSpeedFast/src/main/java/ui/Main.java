package ui;

import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

public class Main {
    public static void main(String[] args) {

        // Instancias
        Pedido p1 = new Pedido("9008", "claudio", "calle perdida");
        Pedido p2 = new PedidoComida("9009", "alejandro", "calle verdadera", "luigi´s", "30 minutos");
        Pedido p3 = new PedidoEncomienda("9010", "maria", "avenida siempre viva", 120.5, 0.8);
        Pedido p4 = new PedidoExpress("9011", "pedro", "pasaje sin nombre", "farmacia cruz roja", 3);

        // Array que contiene los Pedidos
        Pedido[] pedidos = { p1, p2, p3, p4 };

        // Salto de linea
        System.out.println("");
        System.out.println("------------------------------------------------------------------------------------");

        // Recorrido y prueba de metodos sobreescritos y sobrecargados
        for (Pedido p : pedidos) {
            // Reproducir metodo asignarRepartidor a los pedidos con SOBREESCRITURA
            p.asignarRepartidor();

            // reproducir metodo asignarRepartidor , asignando un repartidor mediante SOBRECARGA
            p.asignarRepartidor("Carlos Palma");

            System.out.println("------------------------------------------------------------------------------------");
        }
    }
}