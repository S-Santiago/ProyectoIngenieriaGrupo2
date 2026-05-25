package app;

import controller.LoginController;
import controller.RentabilidadController;
import controller.SesionAplicacion;
import controller.SesionUsuario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loginLoader.load();
            LoginController loginController = loginLoader.getController();

            Stage loginStage = new Stage();
            loginStage.initOwner(primaryStage);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setTitle("Acceso al Analizador");
            loginStage.setScene(new Scene(loginRoot));
            loginController.setStage(loginStage);
            loginController.setOnLoginSuccess(sesion -> {
                SesionAplicacion.establecer(sesion);
                primaryStage.setUserData(sesion);
            });

            loginStage.showAndWait();

            if (primaryStage.getUserData() == null) {
                SesionAplicacion.limpiar();
                javafx.application.Platform.exit();
                return;
            }

            // Cargar datos persistentes solo después de autenticar para mostrar primero el login
            new RentabilidadController().cargarDatos();
            
            // Cargar vista principal con navegación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            SesionUsuario sesion = (SesionUsuario) primaryStage.getUserData();
            controller.NavigationController navigationController = loader.getController();
            if (navigationController != null) {
                navigationController.aplicarSesion(sesion);
            }
            
            Scene scene = new Scene(root, 1280, 800);
            String detalleSesion = sesion == null ? "" : sesion.usuario() + " - " + sesion.rol();
            primaryStage.setTitle("Analizador de Rentabilidad - Sistema de KPIs Financieros - " + detalleSesion);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error al inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}