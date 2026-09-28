package view;

import services.ControladorPedidos;
import model.Pedido;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Ventana que muestra todos los pedidos registrados en una tabla de solo lectura.
// Se actualiza al abrirse, al activarse y con el boton Refrescar.
public class VentanaListaPedidos extends JFrame {

    // Atributos

    // Encabezados de la tabla, en el orden en que se muestran
    private static final String[] COLUMNAS = {
            "ID", "Tipo", "Cliente", "Direccion", "Distancia (km)", "Tiempo est. (min)", "Estado"
    };

    // Controlador
    private final ControladorPedidos controlador;

    // Componentes de la interfaz
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JLabel lblTotal = new JLabel();

    // Constructor
    // Crea la tabla, arma la ventana y la actualiza cada vez que se activa
    public VentanaListaPedidos(ControladorPedidos controlador) {
        super("Listado de pedidos");
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

        // Anchos preferidos por columna, en el mismo orden que COLUMNAS
        int[] anchos = {50, 110, 140, 220, 100, 120, 160};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();

        // Al volver a esta ventana (activarla) la tabla se actualiza
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refrescarTabla();
            }
        });

        refrescarTabla();
        setSize(950, 400);
        setLocationRelativeTo(null);
    }

    // Construccion de la interfaz
    // Crea los botones y los paneles, y conecta los eventos
    private void construirInterfaz() {
        // Botones
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar");

        // Eventos y acciones
        btnRefrescar.addActionListener(e -> refrescarTabla());
        btnCerrar.addActionListener(e -> dispose());

        // Panel de botones alineados a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnCerrar);

        // Panel inferior: total a la izquierda, botones a la derecha
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelInferior.add(lblTotal, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        // Contenido: tabla al centro y panel inferior abajo
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);
        contenido.add(panelInferior, BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    // Acciones
    // Vacia el modelo y lo vuelve a llenar con los datos actuales del controlador.
    // El tiempo estimado se muestra sin decimales
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);

        for (Pedido p : controlador.getPedidos()) {
            modeloTabla.addRow(new Object[]{
                    p.getIdPedido(),
                    p.getTipoPedido(),
                    p.getCliente(),
                    p.getDireccion(),
                    p.getDistanciaKm(),
                    (int) p.calcularTiempoEntrega(),
                    controlador.getEstado(p.getIdPedido())
            });
        }

        lblTotal.setText("Total de pedidos: " + modeloTabla.getRowCount());
    }
}