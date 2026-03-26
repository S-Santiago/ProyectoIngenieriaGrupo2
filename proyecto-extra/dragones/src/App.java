import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<Dragon> dragons = new ArrayList<>();
        
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
                if (Utils.idExists(dragons, id)) {
                    System.out.println("Ya existe un dragón con ese ID.");
                    continue;
                }

                System.out.print("Introduce el nombre del dragón: ");
                String name = scanner.nextLine();
                if (Utils.nameExists(dragons, name)) {
                    System.out.println("Ya existe un dragón con ese nombre.");
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
                if (level < 0) {
                    System.out.println("Nivel no válido. El nivel debe ser un número entero positivo.");
                    continue;
                }

                int attack = Utils.askInt("Introduce el ataque del dragón: ", scanner);
                int defense = Utils.askInt("Introduce la defensa del dragón: ", scanner);

                dragons.add(new Dragon(id, name, type, level, attack, defense));
            } else if (option == 2) {
                for (Dragon dragon : dragons) {
                    System.out.println("ID: " + dragon.id + ", Nombre: " + dragon.name + ", Tipo: " + dragon.type + ", Nivel: " + dragon.level + ", Ataque: " + dragon.attack + ", Defensa: " + dragon.defense);
                }
            } else if (option == 3) {
                int id = Utils.askInt("Introduce el ID del dragón a buscar: ", scanner);
                Dragon foundDragon = null;

                for (Dragon dragon : dragons) {
                    if (dragon.id == id) {
                        foundDragon = dragon;
                        break;
                    }
                }
                if (foundDragon != null) {
                    System.out.println("Dragón encontrado:");
                    System.out.println("ID: " + foundDragon.id + ", Nombre: " + foundDragon.name + ", Tipo: " + foundDragon.type + ", Nivel: " + foundDragon.level + ", Ataque: " + foundDragon.attack + ", Defensa: " + foundDragon.defense);
                } else {
                    System.out.println("Dragón no encontrado.");
                }
            } else if (option == 4) {
                System.out.print("Introduce el nombre del dragón a buscar: ");
                String name = scanner.nextLine();
                Dragon foundDragon = null;

                for (Dragon dragon : dragons) {
                    if (dragon.name.equalsIgnoreCase(name)) {
                        foundDragon = dragon;
                        break;
                    }
                }

                if (foundDragon != null) {
                    System.out.println("Dragón encontrado:");
                    System.out.println("ID: " + foundDragon.id + ", Nombre: " + foundDragon.name + ", Tipo: " + foundDragon.type + ", Nivel: " + foundDragon.level + ", Ataque: " + foundDragon.attack + ", Defensa: " + foundDragon.defense);
                } else {
                    System.out.println("Dragón no encontrado.");
                }
            } else if (option == 5) {
                int id = Utils.askInt("Introduce el ID del dragón a modificar: ", scanner);
                Dragon foundDragon = null;

                for (Dragon dragon : dragons) {
                    if (dragon.id == id) {
                        foundDragon = dragon;
                        break;
                    }
                }

                if (foundDragon != null) {
                    System.out.print("Introduce el nuevo nombre del dragón: ");
                    foundDragon.name = scanner.nextLine();
                    DragonType type = null;
                    while (type == null) {
                        System.out.print("Introduce el nuevo tipo del dragón (" + Utils.arrayToString(DragonType.values()) + "): ");
                        String typeString = scanner.nextLine();
                        try {
                            type = DragonType.valueOf(typeString.toUpperCase());
                        } catch (Exception e) {
                            System.out.println("Tipo no válido.");
                        }
                    }
                    foundDragon.type = type;

                    int level = Utils.askInt("Introduce el nuevo nivel del dragón: ", scanner);
                    foundDragon.level = level;

                    int attack = Utils.askInt("Introduce el nuevo ataque del dragón: ", scanner);
                    foundDragon.attack = attack;

                    int defense = Utils.askInt("Introduce la nueva defensa del dragón: ", scanner);
                    foundDragon.defense = defense;
                } else {
                    System.out.println("Dragón no encontrado.");
                }
            } else if (option == 6) {
                int id = Utils.askInt("Introduce el ID del dragón a eliminar: ", scanner);
                Dragon foundDragon = null;

                for (Dragon dragon : dragons) {
                    if (dragon.id == id) {
                        foundDragon = dragon;
                        break;
                    }
                }

                if (foundDragon != null) {
                    dragons.remove(foundDragon);
                    System.out.println("Dragón eliminado.");
                } else {
                    System.out.println("Dragón no encontrado.");
                }
            } else if (option == 7) {
                System.out.println("Formatos de exportación disponibles:");
                System.out.println("1. CSV");
                System.out.println("2. JSON");
                System.out.println("3. XLSX");

                int exportOption = Utils.askInt("Introduce una opción de exportación: ", scanner);
                if (exportOption == 1) {
                    System.out.print("Exportando a CSV...\nIntroduce el nombre del archivo (con extensión .csv): ");
                    Utils.exportToCSV(dragons, scanner.nextLine());
                } else if (exportOption == 2) {
                    System.out.print("Exportando a JSON...\nIntroduce el nombre del archivo (con extensión .json): ");
                    Utils.exportToJSON(dragons, scanner.nextLine());
                } else if (exportOption == 3) {
                    System.out.print("Exportando a XLSX...\nIntroduce el nombre del archivo (con extensión .xlsx): ");
                    Utils.exportToXLSX(dragons, scanner.nextLine());
                } else {
                    System.out.println("Opción de exportación no válida.");
                }
            } else if (option == 8) {
                System.out.println("Saliendo del programa...");
            } else {
                System.out.println("Opción no válida. Por favor, introduce una opción del 1 al 8.");
            }
        }

        scanner.close();
    }
}
