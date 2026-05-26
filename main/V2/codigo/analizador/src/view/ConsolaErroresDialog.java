package view;

import javax.swing.JOptionPane;

public class ConsolaErroresDialog {

    /**
     * Muestra errores críticos de ejecución (ej. Fichero no encontrado).
     */
    public static void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra errores de validación de negocio (ej. coste negativo, fechas malformadas).
     */
    public static void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Atención: Datos incorrectos detectados", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Muestra mensajes de confirmación o éxito.
     */
    public static void mostrarExito(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Alias de mostrarExito para mostrar información
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        mostrarExito(titulo, mensaje);
    }
}