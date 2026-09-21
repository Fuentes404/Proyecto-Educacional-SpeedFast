package view;

import services.ControladorPedidos;
import model.Pedido;
import util.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

// Ventana con el formulario para registrar pedidos.
// Los campos especificos cambian segun el tipo elegido (Comida, Encomienda o Express).
public class VentanaRegistroPedido extends JFrame {

    // Atributos
    // Tipos de pedido disponibles en el combo
    private static final String TIPO_COMIDA = "Comida";
    private static final String TIPO_ENCOMIENDA = "Encomienda";
    private static final String TIPO_EXPRESS = "Express";

    // Controlador
    private final ControladorPedidos controlador;

    // Componentes de la interfaz: campos comunes a todos los tipos
    private final JTextField txtId = new JTextField(15);
    private final JTextField txtCliente = new JTextField(15);
    private final JTextField txtDireccion = new JTextField(15);
    private final JTextField txtDistancia = new JTextField(15);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{TIPO_COMIDA, TIPO_ENCOMIENDA, TIPO_EXPRESS});

    // Componentes de la interfaz: campos especificos de cada tipo
    private final JTextField txtRestaurante = new JTextField(15);
    private final JTextField txtTiempoPrep = new JTextField(15);
    private final JTextField txtPeso = new JTextField(15);
    private final JTextField txtVolumen = new JTextField(15);
    private final JTextField txtTienda = new JTextField(15);

    // Panel que muestra solo los campos del tipo elegido
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelTipo = new JPanel(cardLayout);

    // Componentes de la interfaz: botones
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnCerrar = new JButton("Cerrar");

    // Constructor
    public VentanaRegistroPedido(ControladorPedidos controlador) {
        super("Registrar pedido");
        this.controlador = controlador;

        // Cerrar esta ventana NO debe cerrar toda la aplicacion
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();
        pack();
        setLocationRelativeTo(null);
    }

    // Construccion de la interfaz

    // Crea el formulario: campos comunes, un panel por tipo de pedido y los botones
    private void construirInterfaz() {
        // Campos comunes
        JPanel panelComun = new JPanel(new GridLayout(0, 2, 8, 8));
        agregarFila(panelComun, "ID:", txtId);
        agregarFila(panelComun, "Cliente:", txtCliente);
        agregarFila(panelComun, "Direccion:", txtDireccion);
        agregarFila(panelComun, "Distancia (km):", txtDistancia);
        agregarFila(panelComun, "Tipo de pedido:", cmbTipo);

        // Campos especificos: comida
        JPanel cardComida = new JPanel(new GridLayout(0, 2, 8, 8));
        agregarFila(cardComida, "Restaurante:", txtRestaurante);
        agregarFila(cardComida, "Tiempo de preparacion:", txtTiempoPrep);
        // Campos especificos: encomienda
        JPanel cardEncomienda = new JPanel(new GridLayout(0, 2, 8, 8));
        agregarFila(cardEncomienda, "Peso (kg):", txtPeso);
        agregarFila(cardEncomienda, "Volumen (m3):", txtVolumen);
        // Campos especificos: express
        JPanel cardExpress = new JPanel(new GridLayout(0, 2, 8, 8));
        agregarFila(cardExpress, "Tienda:", txtTienda);

        // Cada tipo se agrega como una tarjeta del CardLayout
        panelTipo.setBorder(BorderFactory.createTitledBorder("Datos especificos del pedido"));
        panelTipo.add(envolver(cardComida), TIPO_COMIDA);
        panelTipo.add(envolver(cardEncomienda), TIPO_ENCOMIENDA);
        panelTipo.add(envolver(cardExpress), TIPO_EXPRESS);

        // Al cambiar el tipo se muestra la tarjeta correspondiente
        cmbTipo.addActionListener(e -> cardLayout.show(panelTipo, (String) cmbTipo.getSelectedItem()));

        // Panel central: campos comunes arriba y campos del tipo debajo
        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.add(panelComun, BorderLayout.NORTH);
        panelCentro.add(panelTipo, BorderLayout.CENTER);

        // Eventos y acciones de los botones
        btnGuardar.addActionListener(e -> guardar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> dispose());

        // Panel de botones alineados a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        // Contenido: formulario al centro y botones abajo
        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contenido.add(panelCentro, BorderLayout.CENTER);
        contenido.add(panelBotones, BorderLayout.SOUTH);
        setContentPane(contenido);

        // Enter = Guardar
        getRootPane().setDefaultButton(btnGuardar);
    }

    // Agrega una fila: etiqueta + campo a un panel de 2 columnas
    private void agregarFila(JPanel panel, String etiqueta, JComponent campo) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    // Envuelve el panel para que las filas no se estiren cuando la tarjeta tiene pocas filas
    private JPanel envolver(JPanel panel) {
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(panel, BorderLayout.NORTH);
        return envoltorio;
    }

    // Acciones

    // Registra el pedido con los datos del formulario.
    // Pasos: 1) leer los campos  2) validar  3) crear el pedido  4) confirmar  5) limpiar
    private void guardar() {
        // 1. Leer los campos comunes
        String id = txtId.getText().trim();
        String cliente = txtCliente.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String distancia = txtDistancia.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();

        // 2. Validar. El ID se revisa tambien aqui para mostrar un mensaje claro en el formulario;
        // el controlador lo vuelve a validar por seguridad
        String errorId = Validador.validarId(id);
        if (errorId == null && controlador.existeId(id)) {
            errorId = "Ya existe un pedido con el ID \"" + id + "\".";
        }

        List<String> errores = new ArrayList<>();
        errores.add(errorId);
        errores.add(Validador.validarObligatorio("Cliente", cliente));
        errores.add(Validador.validarObligatorio("Direccion", direccion));
        errores.add(Validador.validarNumeroPositivo("Distancia (km)", distancia));

        // Solo se validan los campos del tipo elegido
        if (TIPO_COMIDA.equals(tipo)) {
            errores.add(Validador.validarObligatorio("Restaurante", txtRestaurante.getText()));
            errores.add(Validador.validarObligatorio("Tiempo de preparacion", txtTiempoPrep.getText()));
        } else if (TIPO_ENCOMIENDA.equals(tipo)) {
            errores.add(Validador.validarNumeroPositivo("Peso (kg)", txtPeso.getText()));
            errores.add(Validador.validarNumeroPositivo("Volumen (m3)", txtVolumen.getText()));
        } else {
            errores.add(Validador.validarObligatorio("Tienda", txtTienda.getText()));
        }

        String mensajeError = Validador.unir(errores);
        if (mensajeError != null) {
            JOptionPane.showMessageDialog(this,
                    "Corrija los siguientes datos:\n\n" + mensajeError,
                    "Datos invalidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Crear el pedido en el controlador segun el tipo elegido
        double distanciaKm = Validador.aNumero(distancia);
        Pedido pedido;
        try {
            if (TIPO_COMIDA.equals(tipo)) {
                pedido = controlador.registrarComida(id, cliente, direccion, distanciaKm,
                        txtRestaurante.getText().trim(), txtTiempoPrep.getText().trim());
            } else if (TIPO_ENCOMIENDA.equals(tipo)) {
                pedido = controlador.registrarEncomienda(id, cliente, direccion, distanciaKm,
                        Validador.aNumero(txtPeso.getText()), Validador.aNumero(txtVolumen.getText()));
            } else {
                pedido = controlador.registrarExpress(id, cliente, direccion, distanciaKm,
                        txtTienda.getText().trim());
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Confirmar mostrando el resumen y el tiempo estimado
        JOptionPane.showMessageDialog(this,
                "Pedido registrado con exito\n\n" + pedido.mostrarResumen()
                        + "\nTiempo estimado de entrega: " + (int) pedido.calcularTiempoEntrega() + " min",
                "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);

        // 5. Limpiar el formulario para el siguiente pedido
        limpiarFormulario();
    }

    // Vacia todos los campos, vuelve al primer tipo del combo y deja el cursor en el ID
    private void limpiarFormulario() {
        txtId.setText("");
        txtCliente.setText("");
        txtDireccion.setText("");
        txtDistancia.setText("");
        txtRestaurante.setText("");
        txtTiempoPrep.setText("");
        txtPeso.setText("");
        txtVolumen.setText("");
        txtTienda.setText("");
        cmbTipo.setSelectedIndex(0);
        txtId.requestFocusInWindow();
    }
}