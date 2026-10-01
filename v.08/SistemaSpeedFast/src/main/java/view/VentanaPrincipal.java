package view;

import services.ControladorEntregas;
import services.ControladorPedidos;
import services.ControladorRepartidores;

import javax.swing.*;
import java.awt.*;

// - Función: Ventana principal del sistema, tiene el menú lateral y cambia entre los paneles
public class VentanaPrincipal extends JFrame {

    // — Atributos
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);

    // — Dependencias
    // -- Controladores
    private final ControladorPedidos controladorPedidos = new ControladorPedidos();
    private final ControladorRepartidores controladorRepartidores = new ControladorRepartidores();
    private final ControladorEntregas controladorEntregas = new ControladorEntregas();

    // -- Paneles
    private final PanelRepartidores panelRepartidores = new PanelRepartidores(controladorRepartidores);
    private final PanelPedidos panelPedidos = new PanelPedidos(controladorPedidos);
    private final PanelEntregas panelEntregas = new PanelEntregas(controladorEntregas, controladorRepartidores);

    // — Constructor
    public VentanaPrincipal() {
        // -- Configuraciones de Ventana
        super("SpeedFast - Sistema de Gestión de Entregas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 800);
        setLocationRelativeTo(null);

        // -- Inicializar componentes
        JToggleButton btnRepartidores = new JToggleButton("Repartidores");
        JToggleButton btnPedidos = new JToggleButton("Pedidos");
        JToggleButton btnEntregas = new JToggleButton("Entregas");
        JButton btnSalir = new JButton("Salir");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(btnRepartidores);
        grupo.add(btnPedidos);
        grupo.add(btnEntregas);
        btnPedidos.setSelected(true);

        JPanel menu = new JPanel(new GridLayout(4, 1, 0, 10));
        JPanel lateral = new JPanel(new BorderLayout());

        // -- Configurar Diseño
        contenido.add(panelRepartidores, "repartidores");
        contenido.add(panelPedidos, "pedidos");
        contenido.add(panelEntregas, "entregas");

        menu.add(btnRepartidores);
        menu.add(btnPedidos);
        menu.add(btnEntregas);
        menu.add(btnSalir);

        lateral.setBorder(BorderFactory.createEmptyBorder(100, 15, 15, 15));
        lateral.setPreferredSize(new Dimension(200, 0));
        lateral.add(menu, BorderLayout.NORTH);
        lateral.setOpaque(true);

        add(lateral, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);

        // -- Configurar Los Eventos funcionales
        btnRepartidores.addActionListener(e -> {
            panelRepartidores.actualizarTabla();
            cartas.show(contenido, "repartidores");
        });
        btnPedidos.addActionListener(e -> {
            panelPedidos.actualizarTabla();
            cartas.show(contenido, "pedidos");
        });
        btnEntregas.addActionListener(e -> {
            panelEntregas.actualizarTabla();
            cartas.show(contenido, "entregas");
        });
        btnSalir.addActionListener(e -> System.exit(0));

        // -- Pantalla inicial
        cartas.show(contenido, "pedidos");
    }
}