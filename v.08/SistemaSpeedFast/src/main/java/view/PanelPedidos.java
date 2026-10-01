package view;

import model.EstadoPedido;
import model.TipoPedido;
import services.ControladorPedidos;
import util.ValidadorDatos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// - Función: Panel para gestionar los pedidos, recibe las acciones del usuario y muestra la tabla
public class PanelPedidos extends JPanel {

    // — Constantes
    // Columnas
    private static final String[] COLUMNAS = {
            // Base
            "ID", "Tipo", "Cliente", "Dirección", "Distancia (km)", "Estado",
            // PedidoComida
            "Restaurante", "T. Preparación",
            // PedidoEncomienda
            "Peso (kg)", "Volumen (m3)",
            // PedidoExpress
            "Tienda"
    };

    // — Atributos
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnCancelar = new JButton("Cancelar pedido");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnActualizar = new JButton("Actualizar");

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            // La edicion se hace con el boton Editar
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // — Dependencias
    private final ControladorPedidos controlador;

    // — Controladores

    // -- Nuevo
    // Pide el tipo de pedido, luego sus datos, y crea el pedido
    private void nuevo() {
        Object elegido = JOptionPane.showInputDialog(this, "Tipo de pedido:", "Nuevo pedido",
                JOptionPane.QUESTION_MESSAGE, null, TipoPedido.values(), TipoPedido.COMIDA);
        if (elegido == null) {
            return;
        }
        TipoPedido tipo = (TipoPedido) elegido;

        String[] d = pedirDatos("Nuevo pedido de " + tipo, tipo, null);
        if (d == null) {
            return;
        }

        double distancia = ValidadorDatos.convertirADecimal(d[2]);
        String error;
        switch (tipo) {
            case COMIDA:
                error = controlador.crearComida(d[0], d[1], distancia, d[3], d[4]);
                break;
            case ENCOMIENDA:
                error = controlador.crearEncomienda(d[0], d[1], distancia,
                        ValidadorDatos.convertirADecimal(d[3]),
                        ValidadorDatos.convertirADecimal(d[4]));
                break;
            default:
                error = controlador.crearExpress(d[0], d[1], distancia, d[3]);
        }
        if (error != null) {
            mostrarError(error);
        }
        actualizarTabla();
    }

    // -- Editar
    // Edita el pedido seleccionado, el formulario depende del tipo del pedido
    private void editar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int id = (Integer) modelo.getValueAt(fila, 0);
        TipoPedido tipo = (TipoPedido) modelo.getValueAt(fila, 1);
        EstadoPedido estado = (EstadoPedido) modelo.getValueAt(fila, 5);

        // Se avisa antes de abrir el formulario, el controlador tambien valida esto
        if (estado == EstadoPedido.ENTREGADO) {
            mostrarError("Un pedido entregado no se puede modificar.");
            return;
        }
        if (estado == EstadoPedido.EN_REPARTO) {
            mostrarError("No se puede modificar un pedido que está en reparto.");
            return;
        }
        if (estado == EstadoPedido.CANCELADO) {
            mostrarError("Un pedido cancelado no se puede modificar.");
            return;
        }

        String[] d = pedirDatos("Editar pedido N° " + id, tipo, valoresActuales(fila, tipo));
        if (d == null) {
            return;
        }

        double distancia = ValidadorDatos.convertirADecimal(d[2]);
        String error;
        switch (tipo) {
            case COMIDA:
                error = controlador.editarComida(id, d[0], d[1], distancia, d[3], d[4]);
                break;
            case ENCOMIENDA:
                error = controlador.editarEncomienda(id, d[0], d[1], distancia,
                        ValidadorDatos.convertirADecimal(d[3]),
                        ValidadorDatos.convertirADecimal(d[4]));
                break;
            default:
                error = controlador.editarExpress(id, d[0], d[1], distancia, d[3]);
        }
        if (error != null) {
            mostrarError(error);
        }
        actualizarTabla();
    }

    // -- Cancelar
    // Cancela el pedido seleccionado, el controlador decide si su tipo lo permite
    private void cancelar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int id = (Integer) modelo.getValueAt(fila, 0);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Cancelar el pedido N° " + id + "?",
                "Confirmar cancelación", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        // Si se cancela, se muestra el mensaje propio del tipo de pedido
        String error = controlador.cancelar(id, mensaje ->
                JOptionPane.showMessageDialog(this, mensaje, "Pedido cancelado",
                        JOptionPane.INFORMATION_MESSAGE));
        if (error != null) {
            mostrarError(error);
        }
        actualizarTabla();
    }

    // -- Eliminar
    // Elimina el pedido seleccionado
    private void eliminar() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return;
        }

        int id = (Integer) modelo.getValueAt(fila, 0);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el pedido N° " + id + "?\nSi tiene una entrega registrada, también se eliminará.",
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
    public PanelPedidos(ControladorPedidos controlador) {
        this.controlador = controlador;

        // -- Configuraciones de Ventana
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // -- Inicializar componentes
        JLabel titulo = new JLabel("Gestión de Pedidos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel norte = new JPanel(new BorderLayout(0, 10));

        tabla.setRowHeight(28);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < COLUMNAS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(110);
        }
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(180);

        // -- Configurar Diseño
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnCancelar);
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
        btnCancelar.addActionListener(e -> cancelar());
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

    // Muestra el formulario del tipo, devuelve los datos ya validados o null si cancela
    private String[] pedirDatos(String titulo, TipoPedido tipo, String[] iniciales) {
        String[] etiquetas = etiquetasDe(tipo);
        JTextField[] campos = new JTextField[etiquetas.length];

        JPanel formulario = new JPanel(new GridLayout(etiquetas.length, 2, 10, 10));
        for (int i = 0; i < etiquetas.length; i++) {
            campos[i] = new JTextField(iniciales == null ? "" : iniciales[i], 20);
            formulario.add(new JLabel(etiquetas[i] + ":"));
            formulario.add(campos[i]);
        }

        // Se repite el formulario hasta que los datos sean validos o se cancele
        while (true) {
            int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            String[] datos = new String[campos.length];
            for (int i = 0; i < campos.length; i++) {
                datos[i] = campos[i].getText().trim();
            }

            String error = validar(tipo, datos);
            if (error == null) {
                return datos;
            }
            mostrarError(error);
        }
    }

    // Muestra un mensaje de error o aviso
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // — Métodos Auxiliares

    // Etiquetas del formulario segun el tipo: primero los datos base y luego los propios
    private String[] etiquetasDe(TipoPedido tipo) {
        switch (tipo) {
            case COMIDA:
                return new String[]{"Cliente", "Dirección", "Distancia (km)",
                        "Restaurante", "Tiempo de preparación"};
            case ENCOMIENDA:
                return new String[]{"Cliente", "Dirección", "Distancia (km)",
                        "Peso (kg)", "Volumen (m3)"};
            default:
                return new String[]{"Cliente", "Dirección", "Distancia (km)", "Tienda"};
        }
    }

    // Lee de la tabla los valores actuales del pedido para llenar el formulario
    private String[] valoresActuales(int fila, TipoPedido tipo) {
        String[] valores = new String[etiquetasDe(tipo).length];

        // Datos base: cliente, direccion y distancia
        valores[0] = texto(fila, 2);
        valores[1] = texto(fila, 3);
        valores[2] = texto(fila, 4);

        // Datos propios del tipo
        switch (tipo) {
            case COMIDA:
                valores[3] = texto(fila, 6);
                valores[4] = texto(fila, 7);
                break;
            case ENCOMIENDA:
                valores[3] = texto(fila, 8);
                valores[4] = texto(fila, 9);
                break;
            default:
                valores[3] = texto(fila, 10);
        }
        return valores;
    }

    // Devuelve el valor de una celda como texto, vacio si es null
    private String texto(int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    // Llama al validador que corresponde al tipo de pedido
    private String validar(TipoPedido tipo, String[] d) {
        switch (tipo) {
            case COMIDA:
                return ValidadorDatos.validarPedidoComida(d[0], d[1], d[2], d[3], d[4]);
            case ENCOMIENDA:
                return ValidadorDatos.validarPedidoEncomienda(d[0], d[1], d[2], d[3], d[4]);
            default:
                return ValidadorDatos.validarPedidoExpress(d[0], d[1], d[2], d[3]);
        }
    }

    // Revisa que existan pedidos y que haya una fila seleccionada, devuelve -1 si no
    private int filaSeleccionada() {
        String error = ValidadorDatos.validarLista(controlador.getPedidos());
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