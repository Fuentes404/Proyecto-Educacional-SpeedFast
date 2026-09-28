package view;

import model.Repartidor;
import services.ControladorRepartidores;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

// Ventana para registrar y eliminar repartidores, con la lista de los registrados en una tabla.
public class VentanaRepartidores extends JFrame {

    // Atributos

    // Encabezados de la tabla
    private static final String[] COLUMNAS = {"ID", "Nombre"};

    // Controlador
    private final ControladorRepartidores controlador;

    // Repartidores que muestra la tabla (misma posicion que las filas)
    private List<Repartidor> mostrados = new ArrayList<>();

    // Componentes de la interfaz
    private final JTextField txtNombre = new JTextField(20);
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JLabel lblTotal = new JLabel();

    // Constructor
    public VentanaRepartidores(ControladorRepartidores controlador) {
        super("Gestionar repartidores");
        this.controlador = controlador;

        // Modelo de la tabla: ninguna celda es editable
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();

        // Al volver a esta ventana se actualiza la tabla
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refrescarTabla();
            }
        });

        refrescarTabla();
        setSize(460, 420);
        setLocationRelativeTo(null);
    }

    // Construccion de la interfaz
    private void construirInterfaz() {
        // Panel superior: nombre + boton agregar
        JButton btnAgregar = new JButton("Agregar");
        JPanel panelNuevo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelNuevo.setBorder(BorderFactory.createTitledBorder("Nuevo repartidor"));
        panelNuevo.add(new JLabel("Nombre:"));
        panelNuevo.add(txtNombre);
        panelNuevo.add(btnAgregar);

        // Botones inferiores
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnCerrar = new JButton("Cerrar");

        // Eventos y acciones
        btnAgregar.addActionListener(e -> agregar());
        btnEliminar.addActionListener(e -> eliminar());
        btnCerrar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnAgregar);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelInferior.add(lblTotal, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        // Contenido: formulario arriba, tabla al centro, botones abajo
        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contenido.add(panelNuevo, BorderLayout.NORTH);
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);
        contenido.add(panelInferior, BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    // Acciones

    // Registra el repartidor con el nombre escrito y actualiza la tabla
    private void agregar() {
        try {
            Repartidor nuevo = controlador.registrar(txtNombre.getText());
            txtNombre.setText("");
            txtNombre.requestFocusInWindow();
            refrescarTabla();
            JOptionPane.showMessageDialog(this, "Repartidor registrado: " + nuevo.getNombre()
                    + " (ID " + nuevo.getId() + ")", "Repartidor registrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Elimina el repartidor seleccionado (pide confirmacion)
    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un repartidor de la tabla.",
                    "Sin seleccion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Repartidor repartidor = mostrados.get(fila);

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al repartidor " + repartidor.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminar(repartidor);
            refrescarTabla();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Vacia el modelo y lo vuelve a llenar con los repartidores de la base de datos
    public void refrescarTabla() {
        try {
            mostrados = controlador.getRepartidores();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los repartidores: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloTabla.setRowCount(0);
        for (Repartidor r : mostrados) {
            modeloTabla.addRow(new Object[]{r.getId(), r.getNombre()});
        }
        lblTotal.setText("Total de repartidores: " + modeloTabla.getRowCount());
    }
}