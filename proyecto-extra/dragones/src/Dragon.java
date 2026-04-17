import java.util.ArrayList;
import java.util.List;

public class Dragon {
    private static List<Dragon> dragons = new ArrayList<>();

    public int id;
    public String name;
    public DragonType type;
    public int level;
    public int attack;
    public int defense;

    public Dragon(int id, String name, DragonType type, int level, int attack, int defense) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
    }

    public static String addDragon(int id, String name, DragonType type, int level, int attack, int defense) {
        if (Utils.idExists(dragons, id)) {
            return "Ya existe un dragón con ese ID.";
        }

        if (Utils.nameExists(dragons, name)) {
            return "Ya existe un dragón con ese nombre.";
        }

        dragons.add(new Dragon(id, name, type, level, attack, defense));

        return null;
    }

    public static List<Dragon> getDragons() {
        return dragons;
    }

    public static Dragon findById(int id) {
        for (Dragon dragon : dragons) {
            if (dragon.id == id) {
                return dragon;
            }
        }

        return null;
    }

    public static List<Dragon> findByName(String name) {
        List<Dragon> foundDragons = new ArrayList<>();
        for (Dragon dragon : dragons) {
            if (dragon.name.equalsIgnoreCase(name)) {
                foundDragons.add(dragon);
            }
        }
        return foundDragons;
    }

    public static boolean deleteById(int id) {
        return dragons.removeIf(dragon -> dragon.id == id);
    }

    public static String editDragon(int id, String name, DragonType type, int level, int attack, int defense) {
        Dragon dragon = findById(id);
        if (dragon == null) {
            return "Dragón no encontrado.";
        }

        if (!dragon.name.equalsIgnoreCase(name) && Utils.nameExists(dragons, name)) {
            return "Ya existe un dragón con ese nombre."; // No se puede cambiar a un nombre que ya existe
        }

        dragon.name = name;
        dragon.type = type;
        dragon.level = level;
        dragon.attack = attack;
        dragon.defense = defense;

        return null;
    }
}
