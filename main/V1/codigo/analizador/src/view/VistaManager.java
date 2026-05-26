package view;

import java.io.IOException;

import controller.ExploradorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VistaManager {
    
    private final Stage primaryStage;

    public VistaManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void mostrarImportacionYKpis() {
        cargarVista("/fxml/importacion_kpis.fxml", "Importación y KPIs Globales", null);
    }

    public void mostrarExploradorPedidos() {
        cargarVista("/fxml/explorador_pedidos.fxml", "Explorador de Pedidos", ExploradorController.getInstance());
    }

    public void mostrarPanelRentabilidad() {
        cargarVista("/fxml/panel_rentabilidad.fxml", "Gestión comercial y rentabilidad", null);
    }

    private void cargarVista(String fxmlPath, String titulo, Object controlador) {
        try {
            var recurso = getClass().getResource(fxmlPath);
            if (recurso == null) {
                throw new IOException("No se encontró el recurso FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            
            // Si se proporciona un controlador (para Singletons), asignarlo antes de cargar
            if (controlador != null) {
                loader.setController(controlador);
            }
            
            Parent root = loader.load();
            Scene scene = new Scene(root, 1024, 768);
            primaryStage.setTitle(titulo);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            ConsolaErroresDialog.mostrarError(
                "Error de Navegación", 
                "No se pudo cargar la vista " + titulo + ".\nDetalle: " + e.getMessage()
            );
        } catch (RuntimeException e) {
            ConsolaErroresDialog.mostrarError(
                "Error de Navegación",
                "No se pudo inicializar la vista " + titulo + ".\nDetalle: " + e.getMessage()
            );
        }
    }
}