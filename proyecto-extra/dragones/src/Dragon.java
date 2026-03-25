public class Dragon {
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
}
