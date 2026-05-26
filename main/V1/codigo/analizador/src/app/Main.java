package app;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Forzar renderizado por software para evitar uso de Marlin/Unsafe en Mac
        System.setProperty("prism.order", "sw");
        Application.launch(AppFX.class, args);
    }
}