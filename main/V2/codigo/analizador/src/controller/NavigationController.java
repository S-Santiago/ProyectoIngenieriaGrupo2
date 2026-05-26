package controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import view.ConsolaErroresDialog;

/**
 * Controlador principal que maneja la navegación entre las 3 vistas principales
 * y carga el contenido dinámicamente.
 */
public class NavigationController {

    @FXML
    private VBox centerContent;
    
    @FXML
    private Button btnExploradorPedidos;
    @FXML
    private Button btnImportacionKpis;
    @FXML
    private Button btnRentabilidad;

    @FXML
    public void initialize() {
        // Cargar la primera vista por defecto
        navegarExploradorPedidos();
        aplicarSesion(SesionAplicacion.obtener());
    }

    public void aplicarSesion(SesionUsuario sesion) {
        if (btnImportacionKpis == null || btnRentabilidad == null) {
            return;
        }

        boolean esComercial = sesion != null && sesion.esComercial();
        btnImportacionKpis.setVisible(!esComercial);
        btnImportacionKpis.setManaged(!esComercial);
        btnRentabilidad.setVisible(true);
        btnRentabilidad.setManaged(true);
    }

    @FXML
    public void navegarExploradorPedidos() {
        cargarVista("/fxml/explorador_pedidos.fxml", ExploradorController.getInstance());
    }

    @FXML
    public void navegarImportacionKpis() {
        SesionUsuario sesion = SesionAplicacion.obtener();
        if (sesion != null && sesion.esComercial()) {
            return;
        }
        cargarVista("/fxml/importacion_kpis.fxml", new ImportKpiController());
    }

    @FXML
    public void navegarRentabilidad() {
        cargarVista("/fxml/panel_rentabilidad.fxml", new RentabilidadController());
    }

    @FXML
    public void cerrarAplicacion() {
        Platform.exit();
    }

    private void cargarVista(String fxmlPath, Object controlador) {
        try {
            var recurso = getClass().getResource(fxmlPath);
            if (recurso == null) {
                throw new IOException("No se encontró el recurso FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            
            // Asignar el controlador
            if (controlador != null) {
                loader.setController(controlador);
            }
            
            Parent vista = loader.load();
            
            // Limpiar y cargar la nueva vista
            centerContent.getChildren().clear();
            centerContent.getChildren().add(vista);
            
            // Expandir para llenar el espacio disponible
            VBox.setVgrow(vista, javafx.scene.layout.Priority.ALWAYS);
            
        } catch (IOException e) {
            ConsolaErroresDialog.mostrarError(
                "Error de Navegación", 
                "No se pudo cargar la vista.\nDetalle: " + e.getMessage()
            );
        } catch (RuntimeException e) {
            ConsolaErroresDialog.mostrarError(
                "Error de Navegación",
                "Error al inicializar la vista.\nDetalle: " + e.getMessage()
            );
        }
    }
}