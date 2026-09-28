package view;

import services.ControladorPedidos;
import services.ControladorRepartidores;
import model.Pedido;
import interfaces.Cancelable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

// Ventana para asignar repartidores a pedidos pendientes e iniciar la simulacion de entregas.
public class VentanaEntregas extends JFrame {

    // Atributos
    private static final String SEPARADOR = "---------------------------------------------";

    // Controladores
    private final ControladorPedidos controladorPedidos;
    private final ControladorRepartidores controladorRepartidores;

    // Componentes de la interfaz
    private final JComboBox<Pedido> cmbPedidos = new JComboBox<>();
    private final JComboBox<String> cmbRepartidores = new JComboBox<>();
    private final JButton btnAsignar = new JButton("Asignar repartidor");
    private final JButton btnCancelar = new JButton("Cancelar pedido");
    private final JButton btnIniciar = new JButton("Iniciar entregas");
    private final JButton btnLimpiar = new JButton("Limpiar salida");
    private final JButton btnCerrar = new JButton("Cerrar");
    private final JTextArea txtSalida = new JTextArea(15, 45);

    // Indica si hay una simulacion en curso (bloquea los controles)
    private boolean simulando = false;

    // Constructor
    public VentanaEntregas(ControladorPedidos controladorPedidos, ControladorRepartidores controladorRepartidores) {
        super("Asignar repartidor / Iniciar entrega");
        this.controladorPedidos = controladorPedidos;
        this.controladorRepartidores = controladorRepartidores;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();

        for (String nombre : controladorRepartidores.getNombres()) {
            cmbRepartidores.addItem(nombre);
        }
        cargarPedidos();

        // Al volver a esta ventana se actualiza el combo de pedidos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarPedidos();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    // Construccion de la interfaz
    private void construirInterfaz() {
        // El combo muestra "N° id - tipo - cliente" en vez de la referencia al objeto
        cmbPedidos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pedido) {
                    Pedido p = (Pedido) value;
                    setText("N° " + p.getIdPedido() + " - " + p.getTipoPedido() + " - " + p.getCliente());
                }
                return this;
            }
        });

        // Al cambiar el pedido seleccionado se actualizan los botones segun su tipo (Cancelable)
        cmbPedidos.addActionListener(e -> actualizarControles());

        // Panel superior: asignacion manual y cancelacion
        JPanel panelAsignacion = new JPanel(new GridLayout(0, 2, 8, 8));
        panelAsignacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Gestionar un pedido pendiente"),
                BorderFactory.createEmptyBorder(5, 8, 8, 8)));
        panelAsignacion.add(new JLabel("Pedido:"));
        panelAsignacion.add(cmbPedidos);
        panelAsignacion.add(new JLabel("Repartidor:"));
        panelAsignacion.add(cmbRepartidores);
        panelAsignacion.add(new JLabel());
        panelAsignacion.add(btnAsignar);
        panelAsignacion.add(new JLabel());
        panelAsignacion.add(btnCancelar);

        // Panel central: salida de mensajes
        txtSalida.setEditable(false);
        txtSalida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollSalida = new JScrollPane(txtSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida"));

        // Eventos y acciones de los botones
        btnAsignar.addActionListener(e -> asignar());
        btnCancelar.addActionListener(e -> cancelar());
        btnIniciar.addActionListener(e -> iniciarEntregas());
        btnLimpiar.addActionListener(e -> txtSalida.setText(""));
        btnCerrar.addActionListener(e -> dispose());

        // Panel inferior: botones alineados a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(btnIniciar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        // Contenido: asignacion arriba, salida al centro, botones abajo
        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contenido.add(panelAsignacion, BorderLayout.NORTH);
        contenido.add(scrollSalida, BorderLayout.CENTER);
        contenido.add(panelBotones, BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    // Estado de los controles
    // Llena el combo con los pedidos pendientes y mantiene seleccionado el que estaba elegido
    private void cargarPedidos() {
        Pedido seleccionado = (Pedido) cmbPedidos.getSelectedItem();
        int idSeleccionado = (seleccionado != null) ? seleccionado.getIdPedido() : -1;

        cmbPedidos.removeAllItems();
        Pedido reseleccion = null;
        for (Pedido p : controladorPedidos.getPedidosPendientes()) {
            cmbPedidos.addItem(p);
            if (p.getIdPedido() == idSeleccionado) {
                reseleccion = p;
            }
        }
        if (reseleccion != null) {
            cmbPedidos.setSelectedItem(reseleccion);
        }
        actualizarControles();
    }

    // Habilita o deshabilita los controles segun el pedido seleccionado, su tipo y si hay simulacion en curso
    private void actualizarControles() {
        Pedido seleccionado = (Pedido) cmbPedidos.getSelectedItem();
        boolean hayPedidos = seleccionado != null;

        cmbPedidos.setEnabled(!simulando);
        cmbRepartidores.setEnabled(!simulando);
        btnAsignar.setEnabled(hayPedidos && !simulando);
        btnIniciar.setEnabled(cmbPedidos.getItemCount() > 0 && !simulando);
        // Cancelar solo se habilita si el tipo de pedido implementa Cancelable
        btnCancelar.setEnabled(hayPedidos && !simulando && seleccionado instanceof Cancelable);
    }

    // Cambia el estado de simulacion y actualiza los controles
    private void setSimulando(boolean valor) {
        simulando = valor;
        actualizarControles();
    }

    // Acciones

    // Asigna el repartidor elegido al pedido elegido y muestra el detalle en la salida.
    // Si el controlador rechaza la asignacion, muestra el error en un cuadro de dialogo
    private void asignar() {
        Pedido pedido = (Pedido) cmbPedidos.getSelectedItem();
        String repartidor = (String) cmbRepartidores.getSelectedItem();

        if (pedido == null || repartidor == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido y un repartidor.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String detalle = controladorPedidos.asignarRepartidor(pedido.getIdPedido(), repartidor);
            agregarSalida("Asignacion realizada\n" + SEPARADOR + "\n" + detalle + "\n" + SEPARADOR);
            actualizarControles();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo asignar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cancela el pedido elegido (solo si su tipo implementa Cancelable) y refresca la lista
    private void cancelar() {
        Pedido pedido = (Pedido) cmbPedidos.getSelectedItem();
        if (pedido == null) {
            return;
        }
        try {
            String detalle = controladorPedidos.cancelarPedido(pedido.getIdPedido());
            agregarSalida("Cancelacion realizada\n" + SEPARADOR + "\n" + detalle + "\n" + SEPARADOR);
            cargarPedidos();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo cancelar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inicia la simulacion de entregas de los pedidos pendientes en un hilo secundario
    private void iniciarEntregas() {
        List<Pedido> pendientes = controladorPedidos.getPedidosPendientes();

        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pedidos pendientes para entregar.",
                    "Sin pedidos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!controladorRepartidores.hayRepartidores()) {
            JOptionPane.showMessageDialog(this, "No hay repartidores registrados.",
                    "Sin repartidores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setSimulando(true);
        Map<Integer, String> asignaciones = controladorPedidos.getAsignaciones();

        // Los hilos de los repartidores NO tocan la GUI directamente: encolan el mensaje en el EDT
        Consumer<String> salida = mensaje -> SwingUtilities.invokeLater(() -> agregarSalida(mensaje));

        // SwingWorker: la simulacion (bloqueante) corre fuera del hilo de la interfaz
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controladorRepartidores.simularEntregas(pendientes, asignaciones, salida);
            }

            // Se ejecuta en el EDT cuando termina la simulacion:
            // si salio bien marca los pedidos como entregados y refresca el combo,
            // y en todos los casos vuelve a habilitar los controles
            @Override
            protected void done() {
                try {
                    if (get()) {
                        controladorPedidos.marcarEntregados(pendientes);
                        cargarPedidos();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(VentanaEntregas.this,
                            "Error durante la simulacion: " + ex.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setSimulando(false);
                }
            }
        };
        worker.execute();
    }

    // Agrega una linea al area de salida y baja el scroll hasta el final
    private void agregarSalida(String texto) {
        txtSalida.append(texto + "\n");
        txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
    }
}