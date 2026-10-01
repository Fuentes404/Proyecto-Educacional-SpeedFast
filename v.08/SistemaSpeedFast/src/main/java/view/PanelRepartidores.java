package view;

import services.ControladorRepartidores;
import util.ValidadorDatos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// - Función: Panel para gestionar los repartidores, recibe las acciones del usuario y muestra la tabla
public class PanelRepartidores extends JPanel {

    // — Atributos
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnActualizar = new JButton("Actualizar");

    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    // La edicion se hace con el boton Editar
                    return false;
                }
            };
    private final JTable tabla = new JTable(modelo);

    // — Dependencias
    private final ControladorRepartidores controlador;

    // — Controladores

    // -- Nuevo
    // Pide el nombre y crea el repartidor
    private void nuevo() {
        String nombre = pedirNombre("Nuevo repartidor", "");
        if (nombre != null) {
            String error = controlador.crear(nombre);
            if (error != null) {
                mostrarError(error);
            }
            actualizarTabla();
        }
    }

    // -- Editar
    // Edita el repartidor seleccionado en la tabla
    private void editar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int id = (Integer) modelo.getValueAt(fila, 0);
        String nombreActual = (String) modelo.getValueAt(fila, 1);

        String nombre = pedirNombre("Editar repartidor", nombreActual);
        if (nombre != null) {
            String error = controlador.editar(id, nombre);
            if (error != null) {
                mostrarError(error);
            }
            actualizarTabla();
        }
    }

    // -- Eliminar
    // Elimina el repartidor seleccionado en la tabla
    private void eliminar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int id = (Integer) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 1);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al repartidor " + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            String error = controlador.eliminar(id);
            if (error != null) {
                mostrarError(error);
            }
            actualizarTabla();
        }
    }

    // — Constructor
    public PanelRepartidores(ControladorRepartidores controlador) {
        this.controlador = controlador;

        // -- Configuraciones de Ventana
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // -- Inicializar componentes
        JLabel titulo = new JLabel("Gestión de Repartidores");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel norte = new JPanel(new BorderLayout(0, 10));

        tabla.setRowHeight(28);

        // -- Configurar Diseño
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);

        norte.add(titulo, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // -- Configurar Los Eventos funcionales
        // Acciones de los botones
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> actualizarTabla());

        // -- Carga inicial de datos
        actualizarTabla();
    }

    // — Métodos Funcionales

    // Recarga la tabla desde el controlador
    public void actualizarTabla() {
        String error = controlador.llenarTabla(modelo);
        if (error != null) {
            mostrarError(error);
        }
    }

    // Muestra el formulario del nombre, devuelve null si el usuario cancela
    private String pedirNombre(String titulo, String inicial) {
        JTextField campo = new JTextField(inicial, 20);

        JPanel formulario = new JPanel(new GridLayout(1, 2, 10, 10));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(campo);

        // Se repite el formulario hasta que los datos sean validos o se cancele
        while (true) {
            int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            String error = ValidadorDatos.validarRepartidor(campo.getText());
            if (error == null) {
                return campo.getText().trim();
            }
            mostrarError(error);
        }
    }

    // Muestra un mensaje de error o aviso
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // — Métodos Auxiliares

    // Revisa que existan repartidores y que haya una fila seleccionada, devuelve -1 si no
    private int filaSeleccionada() {
        String error = ValidadorDatos.validarLista(controlador.getRepartidores());
        if (error == null) {
            error = ValidadorDatos.validarSeleccion(tabla.getSelectedRow());
        }
        if (error != null) {
            mostrarError(error);
            return -1;
        }
        return tabla.getSelectedRow();
    }
}