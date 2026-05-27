package controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.EstadoPedido;
import model.LineaPedido;

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

        assertEquals(controller.readAllZones().size(), zonasTableView.getItems().size());
        assertEquals(controller.readAllMarginRules().size(), reglasTableView.getItems().size());
    }

    @Test
    void accesoFinancieroQuedaBloqueadoParaComercial() {
        SesionAplicacion.establecer(new SesionUsuario("comercial", RolUsuario.COMERCIAL, List.of(1)));

        RentabilidadController controller = new RentabilidadController();

        assertFalse(controller.accesoFinancieroPermitido());

        SesionAplicacion.limpiar();
    }

    @Test
    void aplicarSesionOcultaAccesosRestringidosEnLaNavegacion() throws Exception {
        NavigationController controller = new NavigationController();
        setField(controller, "btnImportacionKpis", new Button("Importación"));
        setField(controller, "btnRentabilidad", new Button("Gestión comercial"));

        controller.aplicarSesion(new SesionUsuario("comercial", RolUsuario.COMERCIAL, List.of(1)));

        Button btnImportacionKpis = (Button) getField(controller, "btnImportacionKpis");
        Button btnRentabilidad = (Button) getField(controller, "btnRentabilidad");

        assertFalse(btnImportacionKpis.isVisible());
        assertFalse(btnImportacionKpis.isManaged());
        assertTrue(btnRentabilidad.isVisible());
        assertTrue(btnRentabilidad.isManaged());
    }

    @Test
    void aplicarSesionDeComercialBloqueaGestionYFijaLaZona() throws Exception {
        SesionUsuario sesion = new SesionUsuario("comercial", RolUsuario.COMERCIAL, List.of(2));
        SesionAplicacion.establecer(sesion);

        ExploradorController exploradorController = ExploradorController.getInstance();
        @SuppressWarnings("unchecked")
        List<LineaPedido> pedidos = (List<LineaPedido>) getField(exploradorController, "pedidos");
        pedidos.clear();
        pedidos.add(new LineaPedido(1, 1, "REF-1", "Producto", "Categoria", BigDecimal.TEN, BigDecimal.ONE, 1, "2026-05-25", 2, EstadoPedido.COMPLETADO));

        setField(exploradorController, "tipoFiltroComboBox", new ComboBox<String>());
        setField(exploradorController, "valorFiltroComboBox", new ComboBox<String>());
        setField(exploradorController, "valorFiltroLabel", new Label());
        setField(exploradorController, "valorFiltroTextField", new TextField());
        setField(exploradorController, "fechaInicioLabel", new Label());
        setField(exploradorController, "fechaFinLabel", new Label());
        setField(exploradorController, "fechaInicioDatePicker", new DatePicker());
        setField(exploradorController, "fechaFinDatePicker", new DatePicker());

        ComboBox<String> tipoFiltroComboBox = (ComboBox<String>) getField(exploradorController, "tipoFiltroComboBox");
        tipoFiltroComboBox.getItems().setAll("Todas", "Categoría", "Zona Comercial", "Estado", "Fecha");

        RentabilidadController controller = new RentabilidadController();
        setField(controller, "tabGestion", new Tab("Gestión"));

        controller.aplicarSesion(sesion);

        Tab tabGestion = (Tab) getField(controller, "tabGestion");
        ComboBox<String> valorFiltroComboBox = (ComboBox<String>) getField(exploradorController, "valorFiltroComboBox");

        assertTrue(tabGestion.isDisable());
        assertFalse(tipoFiltroComboBox.isDisable());
        assertFalse(valorFiltroComboBox.isDisable());
        assertNull(tipoFiltroComboBox.getValue());
        assertNull(valorFiltroComboBox.getValue());
        SesionAplicacion.limpiar();
    }

    @Test
    void accesoFinancieroSigueDisponibleParaDirector() {
        SesionAplicacion.establecer(new SesionUsuario("director", RolUsuario.DIRECTOR_FINANCIERO, null));

        RentabilidadController controller = new RentabilidadController();

        assertTrue(controller.accesoFinancieroPermitido());

        SesionAplicacion.limpiar();
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