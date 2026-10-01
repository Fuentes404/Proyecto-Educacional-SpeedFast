package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;

// - Función: Aca se realizan las validaciones de campos
// Cada metodo devuelve el mensaje de error, o null si el dato es valido
public class ValidadorDatos {

    // — Constantes

    // Mensaje que se muestra cuando una tabla no tiene elementos
    public static final String SIN_ENTIDADES = "No existen entidades creadas.";

    // Solo letras (con tildes y ñ) y espacios
    private static final String PATRON_NOMBRE = "^[\\p{L}]+( [\\p{L}]+)*$";

    // Digitos con parte decimal opcional, separada por punto o coma
    private static final String PATRON_NUMERO = "^\\d+([.,]\\d+)?$";

    // — Constructor
    private ValidadorDatos() {
    }

    // — Métodos Funcionales

    // -- Tablas y selección
    // Revisa que la lista tenga elementos
    public static String validarLista(Collection<?> lista) {
        if (lista == null || lista.isEmpty()) {
            return SIN_ENTIDADES;
        }
        return null;
    }

    // Revisa que haya una fila seleccionada en la tabla (JTable devuelve -1 si no hay)
    public static String validarSeleccion(int fila) {
        if (fila < 0) {
            return "Seleccione una fila de la tabla.";
        }
        return null;
    }

    // Revisa que se haya elegido una opcion, por ejemplo en un JComboBox
    public static String validarOpcion(Object opcion, String campo) {
        if (opcion == null) {
            return "Debe seleccionar una opción en el campo " + campo + ".";
        }
        return null;
    }

    // -- Campos de texto
    // Revisa que el texto no sea nulo, vacio ni solo espacios
    public static String validarTextoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El campo " + campo + " no puede estar vacío.";
        }
        return null;
    }

    // Revisa que el texto no este vacio y tenga solo letras y espacios
    public static String validarNombre(String valor, String campo) {
        String error = validarTextoObligatorio(valor, campo);
        if (error != null) {
            return error;
        }
        if (!valor.trim().matches(PATRON_NOMBRE)) {
            return "El campo " + campo + " solo puede contener letras y espacios.";
        }
        return null;
    }

    // -- Campos numéricos
    // Revisa que el texto sea un numero mayor que 0, acepta punto o coma decimal
    public static String validarNumeroPositivo(String valor, String campo) {
        String error = validarTextoObligatorio(valor, campo);
        if (error != null) {
            return error;
        }

        String texto = valor.trim();
        if (!texto.matches(PATRON_NUMERO)) {
            return "El campo " + campo + " debe ser un número (ejemplo: 2,5 o 2.5).";
        }

        double numero = convertirADecimal(texto);
        // Un numero con demasiados digitos puede convertirse en infinito
        if (Double.isInfinite(numero) || Double.isNaN(numero)) {
            return "El campo " + campo + " tiene un valor no válido.";
        }
        if (numero <= 0) {
            return "El campo " + campo + " debe ser mayor que 0.";
        }
        return null;
    }

    // Convierte el texto a double aceptando coma o punto, usar solo despues de validar
    public static double convertirADecimal(String valor) {
        return Double.parseDouble(valor.trim().replace(',', '.'));
    }

    // -- Fecha y hora
    // Revisa que la fecha tenga formato AAAA-MM-DD y no sea anterior a hoy
    public static String validarFecha(String valor) {
        String error = validarTextoObligatorio(valor, "Fecha");
        if (error != null) {
            return error;
        }
        try {
            LocalDate fecha = LocalDate.parse(valor.trim());
            if (fecha.isBefore(LocalDate.now())) {
                return "La fecha no puede ser anterior a hoy.";
            }
        } catch (DateTimeParseException e) {
            return "La fecha debe tener el formato AAAA-MM-DD (ejemplo: 2026-09-29).";
        }
        return null;
    }

    // Revisa que la hora tenga formato HH:mm
    public static String validarHora(String valor) {
        String error = validarTextoObligatorio(valor, "Hora");
        if (error != null) {
            return error;
        }
        try {
            LocalTime.parse(valor.trim());
        } catch (DateTimeParseException e) {
            return "La hora debe tener el formato HH:mm (ejemplo: 14:30).";
        }
        return null;
    }

    // -- Formularios completos
    // Valida los datos de un repartidor
    public static String validarRepartidor(String nombre) {
        return primerError(
                validarNombre(nombre, "Nombre")
        );
    }

    // Valida los datos de un pedido de comida
    public static String validarPedidoComida(String cliente, String direccion, String distancia,
                                             String restaurante, String tiempoPreparacion) {
        return primerError(
                validarNombre(cliente, "Cliente"),
                validarTextoObligatorio(direccion, "Dirección"),
                validarNumeroPositivo(distancia, "Distancia (km)"),
                validarTextoObligatorio(restaurante, "Restaurante"),
                validarTextoObligatorio(tiempoPreparacion, "Tiempo de preparación")
        );
    }

    // Valida los datos de un pedido de encomienda
    public static String validarPedidoEncomienda(String cliente, String direccion, String distancia,
                                                 String peso, String volumen) {
        return primerError(
                validarNombre(cliente, "Cliente"),
                validarTextoObligatorio(direccion, "Dirección"),
                validarNumeroPositivo(distancia, "Distancia (km)"),
                validarNumeroPositivo(peso, "Peso (kg)"),
                validarNumeroPositivo(volumen, "Volumen (m3)")
        );
    }

    // Valida los datos de un pedido express
    public static String validarPedidoExpress(String cliente, String direccion, String distancia,
                                              String tienda) {
        return primerError(
                validarNombre(cliente, "Cliente"),
                validarTextoObligatorio(direccion, "Dirección"),
                validarNumeroPositivo(distancia, "Distancia (km)"),
                validarTextoObligatorio(tienda, "Tienda")
        );
    }

    // Valida los datos de una entrega
    public static String validarEntrega(Object pedido, Object repartidor, String fecha, String hora) {
        return primerError(
                validarOpcion(pedido, "Pedido"),
                validarOpcion(repartidor, "Repartidor"),
                validarFecha(fecha),
                validarHora(hora)
        );
    }

    // — Métodos Auxiliares

    // Devuelve el primer error de la lista, o null si no hay ninguno
    private static String primerError(String... errores) {
        for (String error : errores) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }
}