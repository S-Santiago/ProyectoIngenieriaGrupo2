package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExploradorControllerTest {

    @BeforeAll
    static void iniciarToolkitJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // El toolkit ya está iniciado en el proceso de pruebas.
        }
    }

    @Test
    void actualizarControlesFiltroMuestraElControlAdecuado() throws Exception {
        SesionAplicacion.limpiar();

        ExploradorController controller = ExploradorController.getInstance();

        ComboBox<String> tipoFiltroComboBox = new ComboBox<>();
        ComboBox<String> valorFiltroComboBox = new ComboBox<>();
        TextField valorFiltroTextField = new TextField();
        Label valorFiltroLabel = new Label();
        Label fechaInicioLabel = new Label();
        Label fechaFinLabel = new Label();
        DatePicker fechaInicioDatePicker = new DatePicker();
        DatePicker fechaFinDatePicker = new DatePicker();

        setField(controller, "tipoFiltroComboBox", tipoFiltroComboBox);
        setField(controller, "valorFiltroComboBox", valorFiltroComboBox);
        setField(controller, "valorFiltroTextField", valorFiltroTextField);
        setField(controller, "valorFiltroLabel", valorFiltroLabel);
        setField(controller, "fechaInicioLabel", fechaInicioLabel);
        setField(controller, "fechaFinLabel", fechaFinLabel);
        setField(controller, "fechaInicioDatePicker", fechaInicioDatePicker);
        setField(controller, "fechaFinDatePicker", fechaFinDatePicker);

        tipoFiltroComboBox.getItems().setAll("Todas", "Categoría", "Zona Comercial", "Estado", "Fecha");
        tipoFiltroComboBox.getSelectionModel().select("Categoría");

        controller.actualizarControlesFiltro();

        assertTrue(valorFiltroComboBox.isVisible());
        assertTrue(valorFiltroComboBox.isManaged());
        assertFalse(valorFiltroTextField.isVisible());
        assertFalse(valorFiltroTextField.isManaged());
        assertEquals("Categoría", valorFiltroLabel.getText());
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}