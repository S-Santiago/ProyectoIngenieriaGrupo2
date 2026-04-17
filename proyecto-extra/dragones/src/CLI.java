import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int option = 0;
            
            while (option != 8) {
                System.out.println("Menú:");
                System.out.println("1. Añadir dragón");
                System.out.println("2. Mostrar listado de dragones");
                System.out.println("3. Buscar dragón por ID");
                System.out.println("4. Buscar dragón por nombre");
                System.out.println("5. Modificar dragón");
                System.out.println("6. Eliminar dragón");
                System.out.println("7. Exportar lista de dragones");
                System.out.println("8. Salir");
                
                option = Utils.askInt("Introduce una opción: ", scanner);
                
                if (option == 1) {
                    int id = Utils.askInt("Introduce el ID del dragón: ", scanner);
                    
                    if (Utils.idExists(Dragon.getDragons(), id)) {
                        System.out.println("Error: Ya existe un dragón con el ID " + id + ".");
                        continue;
                    }
                    
                    System.out.print("Introduce el nombre del dragón: ");
                    String name = scanner.nextLine();
                    if (name.trim().isEmpty()) {
                        System.out.println("Error: El nombre no puede estar en blanco.");
                        continue;
                    }
                    
                    if (Utils.nameExists(Dragon.getDragons(), name)) {
                        System.out.println("Error: Ya existe un dragón con ese nombre.");
                        continue;
                    }
                    
                    DragonType type = null;
                    while (type == null) {
                        System.out.print("Introduce el tipo del dragón (" + Utils.arrayToString(DragonType.values()) + "): ");
                        String typeString = scanner.nextLine();
                        try {
                            type = DragonType.valueOf(typeString.toUpperCase());
                        } catch (Exception e) {
                            System.out.println("Tipo no válido.");
                        }
                    }
                    
                    int level = Utils.askInt("Introduce el nivel del dragón: ", scanner);
                    if (level < 0) { System.out.println("Error: Nivel no válido. Debe ser positivo."); continue; }
                    
                    int attack = Utils.askInt("Introduce el ataque del dragón: ", scanner);
                    if (attack < 0) { System.out.println("Error: Ataque no válido. Debe ser positivo."); continue; }
                    
                    int defense = Utils.askInt("Introduce la defensa del dragón: ", scanner);
                    if (defense < 0) { System.out.println("Error: Defensa no válida. Debe ser positiva."); continue; }
                    
                    String resultado = Dragon.addDragon(id, name, type, level, attack, defense);
                    
                    if (resultado != null) {
                        System.out.println("Error: " + resultado);
                    } else {
                        System.out.println("Dragón añadido con éxito!");
                    }
                } else if (option == 2) {
                    System.out.print("¿Quieres aplicar un filtro de búsqueda? (YES o NO): ");
                    String aplicarFiltro = scanner.nextLine();
                    
                    List<Dragon> filteredDragons = new ArrayList<>(Dragon.getDragons());
                    
                    if (aplicarFiltro.equalsIgnoreCase("YES")) {
                        System.out.print("¿Por qué atributo quieres filtrar? (ID, Nombre, Tipo, Nivel, Ataque, Defensa): ");
                        String atributo = scanner.nextLine().toLowerCase();
                        System.out.print("Introduce el valor a buscar: ");
                        String valor = scanner.nextLine();
                        
                        filteredDragons.removeIf(d -> {
                            try {
                                return switch (atributo) {
                                    case "id" -> d.id != Integer.parseInt(valor);
                                    case "nombre" -> !d.name.equalsIgnoreCase(valor);
                                    case "tipo" -> !d.type.name().equalsIgnoreCase(valor);
                                    case "nivel" -> d.level != Integer.parseInt(valor);
                                    case "ataque" -> d.attack != Integer.parseInt(valor);
                                    case "defensa" -> d.defense != Integer.parseInt(valor);
                                    default -> false;
                                };
                            } catch (NumberFormatException e) {
                                return true;
                            }
                        });
                    }
                    
                    // Ordenar por ID de menor a mayor
                    filteredDragons.sort((d1, d2) -> Integer.compare(d1.id, d2.id));
                    
                    System.out.println("\n--- Lista de Dragones ---");
                    if (filteredDragons.isEmpty()) {
                        System.out.println("No hay dragones para mostrar.");
                    } else {
                        for (Dragon dragon : filteredDragons) {
                            System.out.println("ID: " + dragon.id + " | Nombre: " + dragon.name + " | Tipo: " + dragon.type + " | Nivel: " + dragon.level + " | Ataque: " + dragon.attack + " | Defensa: " + dragon.defense);
                        }
                    }
                    
                } else if (option == 3) {
                    int id = Utils.askInt("Introduce el ID del dragón a buscar: ", scanner);
                    Dragon foundDragon = Dragon.findById(id);
                    
                    if (foundDragon != null) {
                        System.out.println("Dragón encontrado: ID: " + foundDragon.id + ", Nombre: " + foundDragon.name + ", Tipo: " + foundDragon.type + ", Nivel: " + foundDragon.level + ", Ataque: " + foundDragon.attack + ", Defensa: " + foundDragon.defense);
                    } else {
                        System.out.println("Dragón no encontrado.");
                    }
                    
                } else if (option == 4) {
                    System.out.print("Introduce el nombre del dragón a buscar: ");
                    String name = scanner.nextLine();
                    List<Dragon> foundDragons = Dragon.findByName(name);
                    
                    if (!foundDragons.isEmpty()) {
                        System.out.println("Dragones encontrados:");
                        for (Dragon foundDragon : foundDragons) {
                            System.out.println("ID: " + foundDragon.id + ", Nombre: " + foundDragon.name + ", Tipo: " + foundDragon.type + ", Nivel: " + foundDragon.level + ", Ataque: " + foundDragon.attack + ", Defensa: " + foundDragon.defense);
                        }
                    } else {
                        System.out.println("Dragón no encontrado.");
                    }
                    
                } else if (option == 5) {
                    System.out.print("¿Quieres buscar el dragón a modificar por ID (1) o por Nombre (2)?: ");
                    String searchType = scanner.nextLine();
                    Dragon foundDragon = null;
                    
                    if (searchType.equals("1")) {
                        int id = Utils.askInt("Introduce el ID: ", scanner);
                        foundDragon = Dragon.findById(id);
                    } else if (searchType.equals("2")) {
                        System.out.print("Introduce el nombre: ");
                        String name = scanner.nextLine();
                        List<Dragon> results = Dragon.findByName(name);
                        if (!results.isEmpty()) foundDragon = results.get(0);
                    } else {
                        System.out.println("Opción no válida.");
                        continue;
                    }
                    
                    if (foundDragon != null) {
                        System.out.print("Introduce el nuevo nombre del dragón (Actual: " + foundDragon.name + "): ");
                        String newName = scanner.nextLine();
                        if (newName.trim().isEmpty()) { System.out.println("Error: El nombre no puede estar vacío."); continue; }
                        
                        if (!foundDragon.name.equalsIgnoreCase(newName) && Utils.nameExists(Dragon.getDragons(), newName)) {
                            System.out.println("Error: Ya existe otro dragón usando ese nombre.");
                            continue;
                        }
                        
                        DragonType type = null;
                        while (type == null) {
                            System.out.print("Introduce el nuevo tipo (" + Utils.arrayToString(DragonType.values()) + ") (Actual: " + foundDragon.type + "): ");
                            try {
                                type = DragonType.valueOf(scanner.nextLine().toUpperCase());
                            } catch (Exception e) {
                                System.out.println("Tipo no válido.");
                            }
                        }
                        
                        int level = Utils.askInt("Introduce el nuevo nivel (Actual: " + foundDragon.level + "): ", scanner);
                        if (level < 0) { System.out.println("Error: Nivel negativo."); continue; }
                        
                        int attack = Utils.askInt("Introduce el nuevo ataque (Actual: " + foundDragon.attack + "): ", scanner);
                        if (attack < 0) { System.out.println("Error: Ataque negativo."); continue; }
                        
                        int defense = Utils.askInt("Introduce la nueva defensa (Actual: " + foundDragon.defense + "): ", scanner);
                        if (defense < 0) { System.out.println("Error: Defensa negativa."); continue; }
                        
                        String resultado = Dragon.editDragon(foundDragon.id, newName, type, level, attack, defense);
                        
                        if (resultado != null) {
                            System.out.println("Error al editar: " + resultado);
                        } else {
                            System.out.println("Dragón modificado con éxito.");
                        }
                    } else {
                        System.out.println("Dragón no encontrado.");
                    }
                } else if (option == 6) {
                    System.out.print("¿Quieres borrar el dragón por ID (1) o por Nombre (2)?: ");
                    String searchType = scanner.nextLine();
                    Dragon foundDragon = null;
                    
                    if (searchType.equals("1")) {
                        int id = Utils.askInt("Introduce el ID a eliminar: ", scanner);
                        foundDragon = Dragon.findById(id);
                    } else if (searchType.equals("2")) {
                        System.out.print("Introduce el nombre a eliminar: ");
                        String name = scanner.nextLine();
                        List<Dragon> results = Dragon.findByName(name);
                        if (!results.isEmpty()) foundDragon = results.get(0);
                    }
                    
                    if (foundDragon != null) {
                        boolean success = Dragon.deleteById(foundDragon.id);
                        if (success) {
                            System.out.println("Dragón " + foundDragon.name + " eliminado correctamente.");
                        } else {
                            System.out.println("Error al intentar eliminar el dragón.");
                        }
                    } else {
                        System.out.println("Error: Dragón no encontrado.");
                    }
                    
                } else if (option == 7) {
                    System.out.println("Formatos de exportación disponibles:");
                    System.out.println("1. CSV");
                    System.out.println("2. JSON");
                    
                    int exportOption = Utils.askInt("Introduce una opción de exportación: ", scanner);
                    if (exportOption == 1) {
                        System.out.print("Introduce el nombre del archivo (ej: dragones.csv): ");
                        String name = scanner.nextLine();
                        if(!name.endsWith(".csv")) name += ".csv";
                        Utils.exportToCSV(Dragon.getDragons(), name);
                        System.out.println("Exportado a CSV.");
                    } else if (exportOption == 2) {
                        System.out.print("Introduce el nombre del archivo (ej: dragones.json): ");
                        String name = scanner.nextLine();
                        if(!name.endsWith(".json")) name += ".json";
                        Utils.exportToJSON(Dragon.getDragons(), name);
                        System.out.println("Exportado a JSON.");
                    } else {
                        System.out.println("Opción de exportación no válida.");
                    }
                    
                } else if (option == 8) {
                    System.out.println("Saliendo de la academia de dragones...");
                } else {
                    System.out.println("Opción no válida. Por favor, introduce una opción del 1 al 8.");
                }
            }
        }
    }
}