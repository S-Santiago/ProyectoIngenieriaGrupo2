package app;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import cli.CliEngine;
import controller.LoginController;
import controller.RentabilidadController;
import controller.SesionAplicacion;
import controller.SesionUsuario;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loginLoader.load();
            LoginController loginController = loginLoader.getController();

            // Título base compartido entre la ventana principal y el diálogo de login
            String tituloBase = "Analizador de Rentabilidad - Sistema de KPIs Financieros";

            Stage loginStage = new Stage();
            loginStage.initOwner(primaryStage);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setTitle(tituloBase);
            Scene loginScene = new Scene(loginRoot);
            aplicarEstilos(loginScene);
            loginStage.setScene(loginScene);
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

            if (mostrarSelectorModoEjecucion()) {
                iniciarCli();
                Platform.exit();
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
            aplicarEstilos(scene);
            String detalleSesion = sesion == null ? "" : sesion.usuario() + " - " + sesion.rol();
            primaryStage.setTitle(tituloBase + " - " + detalleSesion);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al inicializar la aplicación: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error inesperado al inicializar la aplicación: " + e.getMessage());
        }
    }

    private boolean mostrarSelectorModoEjecucion() {
        ButtonType continuarGui = new ButtonType("Seguir con GUI", ButtonBar.ButtonData.OK_DONE);
        ButtonType cambiarCli = new ButtonType("Cambiar a CLI", ButtonBar.ButtonData.OTHER);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Seleccionar modo de uso");
        alert.setHeaderText("Inicio de sesión correcto");
        alert.setContentText("¿Quieres seguir con la interfaz gráfica o cambiar a la consola?");
        alert.getButtonTypes().setAll(continuarGui, cambiarCli);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == cambiarCli;
    }

    private void iniciarCli() {
        CliEngine cliEngine = new CliEngine(new Scanner(System.in));
        Thread cliThread = new Thread(cliEngine::run, "cli-engine");
        cliThread.setDaemon(false);
        cliThread.start();
    }

    private void aplicarEstilos(Scene scene) {
        var recurso = getClass().getResource("/fxml/styles.css");
        if (scene != null && recurso != null) {
            scene.getStylesheets().add(recurso.toExternalForm());
        }
    }
}