package util;

import java.util.List;

// Clase de utilidades para validar los datos del formulario.
// Los metodos validar... devuelven un mensaje de error, o null si el dato es valido.
public final class Validador {

    // Atributos
    // Largo maximo permitido para el ID del pedido
    private static final int LARGO_MAX_ID = 10;

    // Hasta 6 digitos enteros y 3 decimales, con punto o coma (ej: 3.5 / 3,5 / 120)
    private static final String PATRON_NUMERO = "\\d{1,6}([.,]\\d{1,3})?";

    // Constructor
    // Clase de utilidades: no se instancia
    private Validador() {
    }

    // Metodos
    // Valida que un campo de texto no quede vacio.
    // Devuelve: mensaje de error, o null si es valido
    public static String validarObligatorio(String campo, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El campo \"" + campo + "\" es obligatorio.";
        }
        return null;
    }

    // Valida el ID del pedido: obligatorio, alfanumerico (guiones permitidos) y de largo acotado.
    // Devuelve: mensaje de error, o null si es valido
    public static String validarId(String valor) {
        String error = validarObligatorio("ID", valor);
        if (error != null) {
            return error;
        }
        String id = valor.trim();
        if (!id.matches("[A-Za-z0-9-]+")) {
            return "El ID solo puede contener letras, numeros y guiones (sin espacios).";
        }
        if (id.length() > LARGO_MAX_ID) {
            return "El ID no puede tener mas de " + LARGO_MAX_ID + " caracteres.";
        }
        return null;
    }

    // Valida un numero decimal mayor que cero (distancia, peso, volumen).
    // Devuelve: mensaje de error, o null si es valido
    public static String validarNumeroPositivo(String campo, String valor) {
        String error = validarObligatorio(campo, valor);
        if (error != null) {
            return error;
        }
        if (!valor.trim().matches(PATRON_NUMERO)) {
            return "El campo \"" + campo + "\" debe ser un numero valido (ej: 3.5).";
        }
        if (aNumero(valor) <= 0) {
            return "El campo \"" + campo + "\" debe ser mayor que 0.";
        }
        return null;
    }

    // Convierte el texto a double aceptando coma o punto decimal.
    // Usar solo despues de validarNumeroPositivo.
    public static double aNumero(String valor) {
        return Double.parseDouble(valor.trim().replace(',', '.'));
    }

    // Une los errores no nulos en un solo texto (uno por linea, con vineta).
    // Devuelve: el texto con los errores, o null si no hay ninguno
    public static String unir(List<String> errores) {
        StringBuilder sb = new StringBuilder();
        for (String error : errores) {
            if (error != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append("• ").append(error);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}