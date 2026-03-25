import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);

    private static int askInt(String prompt) {
        System.out.print(prompt);

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número entero.");
        }

        return -1;
    }
    public static void main(String[] args) {
        List<Dragon> dragons = new ArrayList<>();
        
        int option = 0;
        
        while (option != 7) {
            System.out.println("Menú:");
            System.out.println("1. Añadir dragón");
            System.out.println("2. Mostrar listado de dragones");
            System.out.println("3. Buscar dragón por ID");
            System.out.println("4. Buscar dragón por nombre");
            System.out.println("5. Modificar dragón");
            System.out.println("6. Eliminar dragón");
            System.out.println("7. Salir");
            
            option = askInt("Introduce una opción: ");

            if (option == 1) {
                int id = askInt("Introduce el ID del dragón: ");
                if (id == -1) {
                    continue;
                }

                System.out.println("Introduce el nombre del dragón:");
                String name = scanner.nextLine();
                System.out.println("Introduce el tipo del dragón:");
                String typeString = scanner.nextLine();
                DragonType type = null;

                while (typeString == null) {
                    try {
                        type = DragonType.valueOf(scanner.nextLine().toUpperCase());
                    } catch (Exception e) {
                        System.out.println("Tipo no válido. Por favor, introduce un tipo válido (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD):");
                        continue;
                    }
                }

                int level = askInt("Introduce el nivel del dragón: ");
                if (level == -1) {
                    continue;
                }

                int attack = askInt("Introduce el ataque del dragón: ");
                if (attack == -1) {
                    continue;
                }

                int defense = askInt("Introduce la defensa del dragón: ");
                if (defense == -1) {
                    continue;
                }

                dragons.add(new Dragon(id, name, type, level, attack, defense));
            } else if (option == 2) {
                for (Dragon dragon : dragons) {
                    System.out.println("ID: " + dragon.id + ", Nombre: " + dragon.name + ", Tipo: " + dragon.type + ", Nivel: " + dragon.level + ", Ataque: " + dragon.attack + ", Defensa: " + dragon.defense);
                }
            } else if (option == 3) {
                int id = askInt("Introduce el ID del dragón a buscar: ");
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
                System.out.println("Introduce el nombre del dragón a buscar: ");
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
                int id = askInt("Introduce el ID del dragón a modificar: ");
                Dragon foundDragon = null;

                for (Dragon dragon : dragons) {
                    if (dragon.id == id) {
                        foundDragon = dragon;
                        break;
                    }
                }

                if (foundDragon != null) {
                    System.out.println("Introduce el nuevo nombre del dragón:");
                    foundDragon.name = scanner.nextLine();
                    System.out.println("Introduce el nuevo tipo del dragón:");
                    String typeString = scanner.nextLine();
                    DragonType type = null;

                    while (typeString == null) {
                        try {
                            type = DragonType.valueOf(scanner.nextLine().toUpperCase());
                        } catch (Exception e) {
                            System.out.println("Tipo no válido. Por favor, introduce un tipo válido (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD):");
                            continue;
                        }
                    }
                    foundDragon.type = type;

                    int level = askInt("Introduce el nuevo nivel del dragón: ");
                    if (level != -1) {
                        foundDragon.level = level;
                    }

                    int attack = askInt("Introduce el nuevo ataque del dragón: ");
                    if (attack != -1) {
                        foundDragon.attack = attack;
                    }

                    int defense = askInt("Introduce la nueva defensa del dragón: ");
                    if (defense != -1) {
                        foundDragon.defense = defense;
                    }
                } else {
                    System.out.println("Dragón no encontrado.");
                }
            } else if (option == 6) {
                int id = askInt("Introduce el ID del dragón a eliminar: ");
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
                System.out.println("Saliendo del programa...");
            } else {
                System.out.println("Opción no válida. Por favor, introduce una opción del 1 al 7.");
            }
        }

        scanner.close();
    }
}
