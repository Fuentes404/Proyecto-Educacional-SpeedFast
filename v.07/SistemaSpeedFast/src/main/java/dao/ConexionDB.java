package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Gestiona la conexion con MySQL y deja lista la base de datos speedfast_db.
public class ConexionDB {

    // Atributos
    // createDatabaseIfNotExist=true: MySQL crea la base si todavia no existe
    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db?createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "casa123";

    // Se ejecuta una sola vez, al usar la clase por primera vez
    static {
        inicializarTablas();
    }

    // Constructor
    // Clase de utilidades: no se instancia
    private ConexionDB() {
    }

    // Metodos

    // Abre y devuelve una conexion nueva.
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Cierra una conexion sin lanzar excepcion (acepta null)
    public static void cerrar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }

    // Crea las tablas si no existen (no borra nada: los datos se conservan entre ejecuciones).
    // Los repartidores se registran desde la aplicacion, por eso no se insertan datos iniciales.
    private static void inicializarTablas() {
        String sqlRepartidor = """
                CREATE TABLE IF NOT EXISTS repartidor (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL
                )
                """;

        // Herencia por tabla unica: el campo tipo indica COMIDA | ENCOMIENDA | EXPRESS
        String sqlPedido = """
                CREATE TABLE IF NOT EXISTS pedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    cliente VARCHAR(100) NOT NULL,
                    direccion VARCHAR(150) NOT NULL,
                    distancia_km DECIMAL(9,3) NOT NULL,
                    tipo VARCHAR(30) NOT NULL,
                    estado VARCHAR(20) NOT NULL,
                    restaurante VARCHAR(100),
                    tiempo_preparacion VARCHAR(50),
                    peso DECIMAL(9,3),
                    volumen DECIMAL(9,3),
                    tienda VARCHAR(100)
                )
                """;

        // entrega depende de pedido y repartidor, por eso se crea al final
        String sqlEntrega = """
                CREATE TABLE IF NOT EXISTS entrega (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_pedido INT NOT NULL,
                    id_repartidor INT NOT NULL,
                    fecha DATE NOT NULL,
                    hora TIME NOT NULL,
                    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
                    FOREIGN KEY (id_repartidor) REFERENCES repartidor(id)
                )
                """;

        Connection con = null;
        try {
            con = conectar();
            try (Statement st = con.createStatement()) {
                st.execute(sqlRepartidor);
                st.execute(sqlPedido);
                st.execute(sqlEntrega);
            }
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        } finally {
            cerrar(con);
        }
    }
}