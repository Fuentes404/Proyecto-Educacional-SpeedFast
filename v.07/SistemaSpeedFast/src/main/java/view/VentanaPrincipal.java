package view;

import services.ControladorPedidos;
import services.ControladorRepartidores;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

// Ventana principal con el menu de la aplicacion.
// Abre las ventanas de registro, listado, repartidores y entregas, compartiendo los mismos controladores.
// Cerrar esta ventana termina la aplicacion.
public class VentanaPrincipal extends JFrame {

    // Controladores (compartidos con todas las ventanas)
    private final ControladorPedidos controladorPedidos;
    private final ControladorRepartidores controladorRepartidores;

    // Ventanas hijas (se reutilizan mientras sigan abiertas)
    private VentanaRegistroPedido ventanaRegistro;
    private VentanaListaPedidos ventanaLista;
    private VentanaRepartidores ventanaRepartidores;
    private VentanaEntregas ventanaEntregas;

    // Constructores

    // inicia los controladores para que esten desde el principio corriendo
    public VentanaPrincipal() {
        this(new ControladorPedidos(), new ControladorRepartidores());
    }

    // Recibe los controladores que se compartiran con las demas ventanas
    public VentanaPrincipal(ControladorPedidos controladorPedidos, ControladorRepartidores controladorRepartidores) {
        super("SpeedFast - Gestion de Entregas");
        this.controladorPedidos = controladorPedidos;
        this.controladorRepartidores = controladorRepartidores;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterfaz();
        setSize(420, 430);
        setLocationRelativeTo(null);
    }

    // Construccion de la interfaz
    private void construirInterfaz() {
        // Titulo
        JLabel lblTitulo = new JLabel("SpeedFast - Gestion de Entregas", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 20f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Botones
        JButton btnRegistrar = new JButton("Registrar pedido");
        JButton btnListar = new JButton("Listar pedidos");
        JButton btnRepartidores = new JButton("Gestionar repartidores");
        JButton btnEntregas = new JButton("Asignar repartidor / Iniciar entrega");
        JButton btnSalir = new JButton("Salir");

        // Eventos y acciones
        btnRegistrar.addActionListener(e -> abrirRegistro());
        btnListar.addActionListener(e -> abrirLista());
        btnRepartidores.addActionListener(e -> abrirRepartidores());
        btnEntregas.addActionListener(e -> abrirEntregas());
        btnSalir.addActionListener(e -> salir());

        // Panel de botones: una columna, cinco filas
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 0, 12));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnListar);
        panelBotones.add(btnRepartidores);
        panelBotones.add(btnEntregas);
        panelBotones.add(btnSalir);

        // Contenido: titulo arriba y botones al centro
        setLayout(new BorderLayout());
        add(lblTitulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
    }

    // Navegacion
    // Si la ventana no existe o ya fue cerrada se crea una nueva;
    // si sigue abierta solo se trae al frente.

    // Abre la ventana de registro de pedidos
    private void abrirRegistro() {
        if (ventanaRegistro == null || !ventanaRegistro.isDisplayable()) {
            ventanaRegistro = new VentanaRegistroPedido(controladorPedidos);
        }
        mostrar(ventanaRegistro);
    }

    // Abre la ventana con el listado de pedidos
    private void abrirLista() {
        if (ventanaLista == null || !ventanaLista.isDisplayable()) {
            ventanaLista = new VentanaListaPedidos(controladorPedidos);
        }
        mostrar(ventanaLista);
    }

    // Abre la ventana de gestion de repartidores
    private void abrirRepartidores() {
        if (ventanaRepartidores == null || !ventanaRepartidores.isDisplayable()) {
            ventanaRepartidores = new VentanaRepartidores(controladorRepartidores);
        }
        mostrar(ventanaRepartidores);
    }

    // Abre la ventana de asignacion de repartidores y entregas
    private void abrirEntregas() {
        if (ventanaEntregas == null || !ventanaEntregas.isDisplayable()) {
            ventanaEntregas = new VentanaEntregas(controladorPedidos, controladorRepartidores);
        }
        mostrar(ventanaEntregas);
    }

    // Muestra la ventana, la restaura si estaba minimizada y la trae al frente
    private void mostrar(JFrame ventana) {
        ventana.setVisible(true);
        ventana.setState(JFrame.NORMAL);
        ventana.toFront();
    }

    // Cierra la aplicacion completa
    private void salir() {
        dispose();
        System.exit(0);
    }
}