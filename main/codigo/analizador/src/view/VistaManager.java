package view;

import java.io.IOException;

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
        cargarVista("/fxml/importacion_kpis.fxml", "Importación y KPIs Globales");
    }

    public void mostrarExploradorPedidos() {
        cargarVista("/fxml/explorador_pedidos.fxml", "Explorador de Pedidos");
    }

    public void mostrarPanelRentabilidad() {
        cargarVista("/fxml/panel_rentabilidad.fxml", "Panel de Rentabilidad");
    }

    private void cargarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
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
        }
    }
}