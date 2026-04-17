import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static int askInt(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número entero.");
            }
        }
    }

    public static String arrayToString(DragonType[] array) {
        String aux = "";
        
        for (DragonType type : array) {
            aux += type + " ";
        }
        
        return aux.trim();
    }

    public static boolean idExists(List<Dragon> dragons, int id) {
        for (Dragon dragon : dragons) {
            if (dragon.id == id) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean nameExists(List<Dragon> dragons, String name) {
        for (Dragon dragon : dragons) {
            if (dragon.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean exportToCSV(List<Dragon> dragons, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("ID,Nombre,Tipo,Nivel,Ataque,Defensa\n");
            for (Dragon dragon : dragons) {
                writer.write(dragon.id + "," + dragon.name + "," + dragon.type + "," + dragon.level + "," + dragon.attack + "," + dragon.defense + "\n");
            }
            writer.close();

            return true;
        } catch (IOException e) {
            System.out.println("Error al exportar a CSV: " + e.getMessage());
        }

        return false;
    }

    public static boolean exportToJSON(List<Dragon> dragons, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("[\n");
            for (int i = 0; i < dragons.size(); i++) {
                Dragon dragon = dragons.get(i);
                writer.write("  {\n");
                writer.write("    \"id\": " + dragon.id + ",\n");
                writer.write("    \"name\": \"" + dragon.name + "\",\n");
                writer.write("    \"type\": \"" + dragon.type + "\",\n");
                writer.write("    \"level\": " + dragon.level + ",\n");
                writer.write("    \"attack\": " + dragon.attack + ",\n");
                writer.write("    \"defense\": " + dragon.defense + "\n");
                writer.write("  }" + (i < dragons.size() - 1 ? "," : "") + "\n");
            }        
            writer.write("]\n");
            writer.close();

            return true;
        } catch (IOException e) {
            System.out.println("Error al exportar a JSON: " + e.getMessage());
        }

        return false;
    }
}
