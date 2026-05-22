package view;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConsolaErroresDialog {

    private static void mostrarDialogo(AlertType tipo, String titulo, String mensaje) {
        Runnable accion = () -> {
            Alert alert = new Alert(tipo);
            Window owner = obtenerOwnerActivo();
            if (owner != null) {
                alert.initOwner(owner);
            }
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setResizable(true);
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

    private static Window obtenerOwnerActivo() {
        List<Window> ventanas = Window.getWindows();
        for (int i = ventanas.size() - 1; i >= 0; i--) {
            Window ventana = ventanas.get(i);
            if (ventana instanceof Stage stage && stage.isShowing()) {
                return stage;
            }
        }
        return null;
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