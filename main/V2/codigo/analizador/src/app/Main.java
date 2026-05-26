package app;

import java.util.Scanner;

import cli.CliEngine;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("¿Cómo deseas iniciar el programa? 1. Interfaz Gráfica (GUI) / 2. Línea de Comandos (CLI): ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> {
                    // Forzar renderizado por software para evitar uso de Marlin/Unsafe en Mac
                    System.setProperty("prism.order", "sw");
                    Application.launch(AppFX.class, args);
                }
                case "2" -> {
                    new CliEngine(scanner).run();
                    return;
                }
                default -> System.out.println("Opción no válida. Escribe 1 para GUI o 2 para CLI.");
            }
        }
    }
}