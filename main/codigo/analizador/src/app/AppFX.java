package app;

import javafx.application.Application;
import javafx.stage.Stage;
import view.VistaManager;

public class AppFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        VistaManager vistaManager = new VistaManager(primaryStage);
        vistaManager.mostrarExploradorPedidos();
    }
}