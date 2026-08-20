package ui;

import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

public class Main {
    public static void main(String[] args) {

        // Instancias:
        Pedido p1 = new PedidoComida("9009", "Alejandro", "Calle Verdadera 123", 4.5,
                "Luigi's Pizza", "30 minutos");
        Pedido p2 = new PedidoEncomienda("9010", "Maria", "Avenida Siempre Viva 742", 12.0,
                120.5, 0.8);
        Pedido p3 = new PedidoExpress("9011", "Pedro", "Pasaje Sin Nombre 45", 8.0,
                "Farmacia Cruz Roja");

        // Array que contiene los pedidos
        Pedido[] pedidos = { p1, p2, p3 };

        System.out.println("- Resumen de Pedidos -");
        System.out.println("--------------------------------------------------");

        // Recorrido mostrarResumen() + asignarRepartidor() sobreescrito y sobrecargado
        for (Pedido p : pedidos) {
            // Metodos asignados en la semana 2
            p.mostrarResumen();
            p.calcularTiempoEntrega();

            // Metodos Asignados en la Semana 1
            // actualizados en la clase abstracta
            p.asignarRepartidor();
            p.asignarRepartidor("Carlos Palma");
            System.out.println("---------------------------------------------------------------------");
        }

    }
}