package main;

import view.VentanaPrincipal;

import javax.swing.SwingUtilities;

// tiempo trabajado en el proyecto: 32hr

public class Main {
    public static void main(String[] args) {
        // inicializa la aplicacion abriendo la ventana principal
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}