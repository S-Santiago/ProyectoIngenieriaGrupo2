package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RentabilidadControllerTest {

    @BeforeAll
    static void iniciarToolkitJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // El toolkit ya está iniciado en el proceso de pruebas.
        }
    }

    @Test
    void refrescarGestionCargaLasTablasConDatosPersistidos() throws Exception {
        RentabilidadController controller = new RentabilidadController();

        TableView zonasTableView = new TableView();
        TableView reglasTableView = new TableView();

        setField(controller, "zonasTableView", zonasTableView);
        setField(controller, "reglasTableView", reglasTableView);
        setField(controller, "zonaIdField", new TextField());
        setField(controller, "zonaNombreField", new TextField());
        setField(controller, "zonaPaisField", new TextField());
        setField(controller, "zonaResponsableField", new TextField());
        setField(controller, "zonaObjetivoField", new TextField());
        setField(controller, "reglaIdField", new TextField());
        setField(controller, "reglaCategoriaField", new TextField());
        setField(controller, "reglaMargenField", new TextField());
        setField(controller, "reglaActivaCheckBox", new CheckBox());
        setField(controller, "reglaDescripcionTextArea", new TextArea());

        @SuppressWarnings("unchecked")
        ObservableList zonasItems = (ObservableList) getField(controller, "zonasItems");
        @SuppressWarnings("unchecked")
        ObservableList reglasItems = (ObservableList) getField(controller, "reglasItems");

        zonasTableView.setItems(zonasItems);
        reglasTableView.setItems(reglasItems);

        controller.cargarDatos();
        controller.refrescarGestion();

        assertEquals(5, zonasTableView.getItems().size());
        assertEquals(5, reglasTableView.getItems().size());
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}