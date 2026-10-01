package services;

import dao.ConexionBD;
import dao.EntregaDAO;
import dao.RepartidorDAO;
import model.Repartidor;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// - Función: Controla los repartidores (crear, editar y eliminar) y aplica sus reglas del negocio
public class ControladorRepartidores {

    // — Dependencias
    // Acceso a la base de datos
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    // — Métodos de Entrada

    // -- Crear
    // Crea un repartidor nuevo, devuelve el error o null si todo salio bien
    public String crear(String nombre) {
        try {
            repartidorDAO.guardar(new Repartidor(nombre));
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Editar
    // Cambia el nombre de un repartidor, devuelve el error o null si todo salio bien
    public String editar(int id, String nuevoNombre) {
        try {
            Repartidor repartidor = repartidorDAO.buscarPorId(id);
            if (repartidor == null) {
                return "El repartidor no existe.";
            }
            repartidor.setNombre(nuevoNombre);
            repartidorDAO.actualizar(repartidor);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // -- Eliminar
    // Elimina un repartidor, devuelve el error o null si todo salio bien
    public String eliminar(int id) {
        try {
            if (repartidorDAO.buscarPorId(id) == null) {
                return "El repartidor no existe.";
            }
            // No se elimina si tiene entregas pendientes o en reparto
            if (entregaDAO.tieneEntregasEnProceso(id)) {
                return "No se puede eliminar: el repartidor tiene entregas en proceso.";
            }
            // Sus entregas terminadas se eliminan junto con el
            entregaDAO.eliminarPorRepartidor(id);
            repartidorDAO.eliminar(id);
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }

    // — Métodos de Salida

    // -- Consultas
    // Busca un repartidor por su ID, devuelve null si no existe o si falla la base de datos
    public Repartidor buscarPorId(int id) {
        try {
            return repartidorDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return null;
        }
    }

    // Lista de repartidores para consultas y selectores, vacia si falla la base de datos
    public List<Repartidor> getRepartidores() {
        try {
            return repartidorDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println(ConexionBD.mensajeError(e));
            return new ArrayList<>();
        }
    }

    // -- Tabla
    // Llena la tabla del panel, devuelve el error o null si todo salio bien
    public String llenarTabla(DefaultTableModel modelo) {
        try {
            List<Repartidor> repartidores = repartidorDAO.listarTodos();
            modelo.setRowCount(0);
            for (Repartidor repartidor : repartidores) {
                modelo.addRow(new Object[]{
                        repartidor.getId(),
                        repartidor.getNombre()
                });
            }
            return null;
        } catch (SQLException e) {
            return ConexionBD.mensajeError(e);
        }
    }
}