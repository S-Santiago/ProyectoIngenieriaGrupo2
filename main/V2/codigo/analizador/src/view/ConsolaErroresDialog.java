package view;

import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ConsolaErroresDialog {

    public static final String CLAVE_ANALISIS_MARGEN = "analisis_margen";
    public static final String CLAVE_DESVIACIONES_ZONAS = "desviaciones_zonas";

    private static final Preferences PREFERENCIAS = Preferences.userNodeForPackage(ConsolaErroresDialog.class);
    private static final String PREFIJO_NO_MOSTRAR = "no_mostrar_";

    private static void mostrarDialogo(AlertType tipo, String titulo, String mensaje) {
        Runnable accion = () -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            accion.run();
        } else {
            Platform.runLater(accion);
        }
    }

    private static String clavePreferencia(String clave) {
        return PREFIJO_NO_MOSTRAR + clave;
    }

    private static boolean noVolverAMostrar(String clave) {
        return PREFERENCIAS.getBoolean(clavePreferencia(clave), false);
    }

    private static void marcarNoVolverAMostrar(String clave) {
        PREFERENCIAS.putBoolean(clavePreferencia(clave), true);
    }

    public static void reiniciarNoVolverAMostrar(String clave) {
        PREFERENCIAS.remove(clavePreferencia(clave));
    }

    public static void reiniciarNoVolverAMostrarAnalisis() {
        reiniciarNoVolverAMostrar(CLAVE_ANALISIS_MARGEN);
        reiniciarNoVolverAMostrar(CLAVE_DESVIACIONES_ZONAS);
    }

    public static void mostrarInfoConOpcionNoMostrar(String clave, String titulo, String mensaje) {
        if (noVolverAMostrar(clave)) {
            return;
        }

        Runnable accion = () -> {
            if (noVolverAMostrar(clave)) {
                return;
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(titulo);

            Label mensajeLabel = new Label(mensaje);
            mensajeLabel.setWrapText(true);
            mensajeLabel.setMaxWidth(Double.MAX_VALUE);

            CheckBox noMostrarCheckBox = new CheckBox("No volver a mostrar");
            VBox contenido = new VBox(12, mensajeLabel, noMostrarCheckBox);
            contenido.setPadding(new Insets(8, 0, 0, 0));
            contenido.setPrefWidth(420);

            alert.getDialogPane().setContent(contenido);
            alert.showAndWait();

            if (noMostrarCheckBox.isSelected()) {
                marcarNoVolverAMostrar(clave);
            }
        };

        if (Platform.isFxApplicationThread()) {
            accion.run();
        } else {
            Platform.runLater(accion);
        }
    }

    /**
     * Muestra errores críticos de ejecución (ej. Fichero no encontrado).
     */
    public static void mostrarError(String titulo, String mensaje) {
        mostrarDialogo(AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Muestra advertencias no bloqueantes.
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        mostrarDialogo(AlertType.WARNING, titulo, mensaje);
    }

    /**
     * Muestra errores de validación de negocio (ej. coste negativo, fechas malformadas).
     */
    public static void mostrarValidacion(String mensaje) {
        mostrarAdvertencia("Atención: datos incorrectos detectados", mensaje);
    }
    
    /**
     * Muestra mensajes de confirmación o éxito.
     */
    public static void mostrarExito(String titulo, String mensaje) {
        mostrarDialogo(AlertType.INFORMATION, titulo, mensaje);
    }

    /**
     * Alias de mostrarExito para mostrar información
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        mostrarExito(titulo, mensaje);
    }
}