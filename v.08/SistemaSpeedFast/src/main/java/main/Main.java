package main;

import dao.ConexionBD;
import view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

// - Función: Arranca el sistema y prepara la aplicación.
public class Main {
    public static void main(String[] args) {

        // — Metodo de arranque: punto donde inicia el programa
        // Comprueba la conexion con MySQL y lo informa por consola
        try (Connection conexion = ConexionBD.conectar()) {
            System.out.println("Conexion exitosa con la base de datos: " + conexion.getCatalog());
        } catch (SQLException e) {
            System.out.println(ConexionBD.mensajeError(e));
        }

        // — Métodos de apertura: mostrar la aplicación
        // Abre la ventana principal (la ventana se crea y se muestra en la misma línea)
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}