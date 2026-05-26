package app;

import controller.RentabilidadController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar datos persistentes
            new RentabilidadController().cargarDatos();
            
            // Cargar vista principal con navegación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1280, 800);
            primaryStage.setTitle("Analizador de Rentabilidad - Sistema de KPIs Financieros");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error al inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}