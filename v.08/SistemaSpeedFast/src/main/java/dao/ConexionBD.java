package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// - Función: Conexion entre la aplicacion SpeedFast y la base de datos MySQL
public class ConexionBD {

    // — Conexión a Base de datos
    // Datos de conexion
    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = "casa123";

    // — Constructor
    private ConexionBD() {
    }

    // — Métodos de conexión

    // Abre una conexion nueva, cada DAO la cierra al terminar su consulta
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // — Métodos Auxiliares

    // Convierte un error de MySQL en el mensaje que ven los controladores y las vistas
    public static String mensajeError(SQLException e) {
        return "Error de base de datos: " + e.getMessage();
    }
}