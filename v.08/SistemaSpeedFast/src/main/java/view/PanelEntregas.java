package view;

import model.Entrega;
import model.EstadoPedido;
import model.Pedido;
import model.Repartidor;
import services.ControladorEntregas;
import services.ControladorRepartidores;
import util.ValidadorDatos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// - Función: Panel para gestionar las entregas, recibe las acciones del usuario y muestra la tabla y el seguimiento
public class PanelEntregas extends JPanel {

    // — Atributos
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEnviar = new JButton("Enviar pedido");

    // Columnas tablas
    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora", "Estado pedido"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    // La edicion se hace con el boton Editar
                    return false;
                }
            };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea areaSalida = new JTextArea(6, 40);

    // — Dependencias
    private final ControladorEntregas controlador;
    private final ControladorRepartidores controladorRepartidores;

    // — Controladores

    // -- Nuevo
    // Asigna un pedido pendiente a un repartidor
    private void nuevo() {
        List<Pedido> pedidos = controlador.getPedidosDisponibles();
        if (pedidos.isEmpty()) {
            mostrarError("No hay pedidos pendientes disponibles para asignar.");
            return;
        }
        String errorLista = ValidadorDatos.validarLista(controladorRepartidores.getRepartidores());
        if (errorLista != null) {
            mostrarError(errorLista + " Cree un repartidor primero.");
            return;
        }

        JComboBox<Pedido> comboPedido = new JComboBox<>(pedidos.toArray(new Pedido[0]));
        JComboBox<Repartidor> comboRepartidor = new JComboBox<>(controladorRepartidores.getRepartidores().toArray(new Repartidor[0]));
        comboPedido.setRenderer(crearRenderer());
        comboRepartidor.setRenderer(crearRenderer());

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 10));
        formulario.add(new JLabel("Pedido:"));
        formulario.add(comboPedido);
        formulario.add(new JLabel("Repartidor:"));
        formulario.add(comboRepartidor);

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Nueva entrega",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        Pedido pedido = (Pedido) comboPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) comboRepartidor.getSelectedItem();

        String error = controlador.crear(pedido.getIdPedido(), repartidor.getId());
        if (error != null) {
            mostrarError(error);
        }
        actualizarTabla();
    }

    // -- Editar
    // Cambia repartidor, fecha y hora de la entrega seleccionada
    private void editar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int idEntrega = (Integer) modelo.getValueAt(fila, 0);
        EstadoPedido estado = (EstadoPedido) modelo.getValueAt(fila, 5);

        // Se avisa antes de abrir el formulario
        if (estado != EstadoPedido.PENDIENTE) {
            mostrarError("Solo se pueden editar entregas de pedidos pendientes.");
            return;
        }

        Entrega entrega = controlador.buscarPorId(idEntrega);
        if (entrega == null) {
            mostrarError("La entrega no existe.");
            return;
        }

        // Los repartidores vienen de la base de datos: se selecciona el actual comparando por ID
        List<Repartidor> repartidores = controladorRepartidores.getRepartidores();
        JComboBox<Repartidor> comboRepartidor = new JComboBox<>(repartidores.toArray(new Repartidor[0]));
        comboRepartidor.setRenderer(crearRenderer());
        for (Repartidor r : repartidores) {
            if (r.getId() == entrega.getIdRepartidor()) {
                comboRepartidor.setSelectedItem(r);
            }
        }

        JTextField campoFecha = new JTextField(entrega.getFecha().toString(), 15);
        JTextField campoHora = new JTextField(entrega.getHora().withSecond(0).withNano(0).toString(), 15);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 10));
        formulario.add(new JLabel("Repartidor:"));
        formulario.add(comboRepartidor);
        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        formulario.add(campoFecha);
        formulario.add(new JLabel("Hora (HH:mm):"));
        formulario.add(campoHora);

        // Se repite el formulario hasta que los datos sean validos o se cancele
        while (true) {
            int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar entrega N° " + idEntrega,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            Repartidor repartidor = (Repartidor) comboRepartidor.getSelectedItem();
            String error = ValidadorDatos.validarEntrega(entrega.getIdPedido(), repartidor,
                    campoFecha.getText(), campoHora.getText());

            if (error == null) {
                error = controlador.editar(idEntrega, repartidor.getId(),
                        LocalDate.parse(campoFecha.getText().trim()),
                        LocalTime.parse(campoHora.getText().trim()));
                if (error != null) {
                    mostrarError(error);
                }
                actualizarTabla();
                return;
            }
            mostrarError(error);
        }
    }

    // -- Eliminar
    // Deshace la asignacion de la entrega seleccionada
    private void eliminar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int idEntrega = (Integer) modelo.getValueAt(fila, 0);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la entrega N° " + idEntrega + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            String error = controlador.eliminar(idEntrega);
            if (error != null) {
                mostrarError(error);
            }
            actualizarTabla();
        }
    }

    // -- Enviar
    // Envia todos los pedidos pendientes del repartidor de la entrega seleccionada
    private void enviar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int idEntrega = (Integer) modelo.getValueAt(fila, 0);
        Object nombre = modelo.getValueAt(fila, 2);
        Entrega entrega = controlador.buscarPorId(idEntrega);
        if (entrega == null) {
            mostrarError("La entrega no existe.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "Se enviarán todos los pedidos pendientes de " + nombre + ".\n¿Continuar?",
                "Enviar pedidos", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        String error = controlador.enviar(entrega.getIdRepartidor(), this::agregarMensaje);
        if (error != null) {
            mostrarError(error);
        }
        actualizarTabla();
    }

    // — Constructor
    public PanelEntregas(ControladorEntregas controlador, ControladorRepartidores controladorRepartidores) {
        this.controlador = controlador;
        this.controladorRepartidores = controladorRepartidores;

        // -- Configuraciones de Ventana
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // -- Inicializar componentes
        JLabel titulo = new JLabel("Gestión de Entregas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel norte = new JPanel(new BorderLayout(0, 10));

        tabla.setRowHeight(28);

        areaSalida.setEditable(false);
        JScrollPane scrollSalida = new JScrollPane(areaSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Seguimiento de entregas"));

        // -- Configurar Diseño
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);
        botones.add(btnEnviar);

        norte.add(titulo, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(scrollSalida, BorderLayout.SOUTH);

        // -- Configurar Los Eventos funcionales
        // Acciones de los botones
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> actualizarTabla());
        btnEnviar.addActionListener(e -> enviar());

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

    // Seguro para llamar desde cualquier hilo (los Repartidor corren en hilos aparte)
    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaSalida.append(mensaje + "\n");
            areaSalida.setCaretPosition(areaSalida.getDocument().getLength());
        });
    }

    // Muestra un mensaje de error o aviso
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // — Métodos Auxiliares

    // Muestra en los selectores un texto legible en vez del objeto
    private DefaultListCellRenderer crearRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                                                          int indice, boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Pedido) {
                    Pedido p = (Pedido) valor;
                    setText("N° " + p.getIdPedido() + " - " + p.getTipoPedido() + " - " + p.getCliente());
                } else if (valor instanceof Repartidor) {
                    setText(((Repartidor) valor).getNombre());
                }
                return this;
            }
        };
    }

    // Revisa que existan entregas y que haya una fila seleccionada
    private int filaSeleccionada() {
        String error = ValidadorDatos.validarLista(controlador.getEntregas());
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