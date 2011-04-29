import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(authors = {"Powerbot Scripters Team"}, name = "Power Slayer", version = 0.1, description = "Slayer bot.")
public class powerSlayer extends Script implements PaintListener, MouseListener {
    private Task currentTask;
    private int weaponSpecUsage = -1;
    private List<String> pickup = new ArrayList<String>();
    private RSNPC currentMonster;
    private int tab = 1;

    private enum SlayerMaster {
        MAZCHNA("Mazchna", new RSTile(0, 0), 20),
        TURAEL("Turael", new RSTile(0, 0), 3);
        private RSTile location;
        private String name;
        private int combatLevel;
        private int slayerLevel;

        private SlayerMaster(String name, RSTile location, int combatLevel) {
            this.name = name;
            this.location = location;
            this.combatLevel = combatLevel;
            this.slayerLevel = 0;
        }

        private SlayerMaster(String name, RSTile location, int combatLevel, int slayerLevel) {
            this.name = name;
            this.location = location;
            this.combatLevel = combatLevel;
            this.slayerLevel = slayerLevel;
        }

        public SlayerMaster get(String name) {
            for (SlayerMaster master : values()) {
                if (name.toLowerCase().contains(master.getName().toLowerCase())) {
                    return master;
                }
            }
            return null;
        }

        public RSTile getLocation() {
            return this.location;
        }

        public String getName() {
            return this.name;
        }

        private int getSlayerLevel() {
            return this.slayerLevel;
        }

        private int getCombatLevel() {
            return this.combatLevel;
        }
    }

    private static enum Monsters {
        ABBERANT_SPECTRE("Aberrant Spectre", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Slayer helmet", "Nosepeg"}, org.rsbot.script.methods.Equipment.HELMET))),
        ABYSSAL_DEMON("Abyssal Demon", new Location(new RSTile(0, 0), 0)),
        AQUANITE("Aquanite", new Location(new RSTile(0, 0), 0)),
        ANKOU("Ankou", new Location(new RSTile(0, 0), 0)),
        BANSHEE("Banshee", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Earmuffs", "Masked earmuffs", "Slayer helmet"}, org.rsbot.script.methods.Equipment.HELMET))),
        BASILISK("Basilisk", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Mirror shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        BAT("Bat", new Location(new RSTile(0, 0), 0)),
        BEAR("Black bear", new Location(new RSTile(0, 0), 0)),
        BIRD("Chicken", new Location(new RSTile(0, 0), 0)),
        BLACK_DEMON("Black demon", new Location(new RSTile(0, 0), 0)),
        BLACK_DRAGON("Black dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        BLOODVELD("Bloodveld", new Location(new RSTile(0, 0), 0)),
        BLUE_DRAGON("Blue dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        BRINE_RAT("Brine rat", new Location(new RSTile(0, 0), 0)),
        BRONZE_DRAGON("Bronze dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        CATABLEPON("Catablepon", new Location(new RSTile(0, 0), 0)),
        CAVE_BUG("Cave bug", new Location(new RSTile(0, 0), 0)),
        CAVE_CRAWLER("Cave crawler", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Antipoison (1)", "Antipoison (2)", "Antipoison (3)", "Antipoison (4)"}))),
        CAVE_HORROR("Cave horror", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Witchwood icon"}, org.rsbot.script.methods.Equipment.NECK))),
        CAVE_SLIME("Cave slime", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Antipoison (1)", "Antipoison (2)", "Antipoison (3)", "Antipoison (4)"}))),
        COCKATRICE("Cockatrice", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Mirror sheild"}, org.rsbot.script.methods.Equipment.SHIELD))),
        COW("Cow", new Location(new RSTile(0, 0), 0)),
        CROCODILE("Crocodile", new Location(new RSTile(0, 0), 0), new Requirements(new Item[]{new Item(new String[]{"Waterskin (1)", "Waterskin (2)", "Waterskin (3)", "Waterskin (4)"}), new Item(new String[]{"Ice coolers"})}, new Finisher(new String[]{"Rock hammer"}))),
        DRAGANNOTH("Dragannoth", new Location(new RSTile(0, 0), 0)),
        DARK_BEAST("Dark beast", new Location(new RSTile(0, 0), 0)),
        //Moruner Armour...
        DESERT_LIZARD("Desert lizard", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Waterskin (1)", "Waterskin (2)", "Waterskin (3)", "Waterskin (4)"}))),
        DOG("Guard dog", new Location(new RSTile(0, 0), 0)),
        DUST_DEVIL("Dust devil", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Slayer helmet", "Facemask"}, org.rsbot.script.methods.Equipment.HELMET))),
        DWARF("Dwarf", new Location(new RSTile(0, 0), 0)),
        EARTH_WARRIOR("Earth warrior", new Location(new RSTile(0, 0), 0)),
        ELF("Elf warrior", new Location(new RSTile(0, 0), 0)),
        FEVER_SPIDER("Fever spider", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Slayer gloves"}, org.rsbot.script.methods.Equipment.HANDS))),
        FIRE_GIANTS("Fire giants", new Location(new RSTile(0, 0), 0)),
        FLESH_CRAWLERS("Flesh crawlers", new Location(new RSTile(0, 0), 0)),
        GARGOYLE("Gargoyle", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Rock hammer"}), new Finisher(new String[]{"Rock hammer"}))),
        GHOST("Ghost", new Location(new RSTile(0, 0), 0)),
        GHOUL("Ghoul", new Location(new RSTile(0, 0), 0)),
        GIANT_SPIDER("Giant spider", new Location(new RSTile(0, 0), 0)),
        GOBLIN("Goblin", new Location(new RSTile(0, 0), 0)),
        GORAK("Gorak", new Location(new RSTile(0, 0), 0)),
        GREATER_DEMON("Greater demon", new Location(new RSTile(0, 0), 0)),
        GREEN_DRAGON("Green dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        HARPIE_BUG_SWARM("Harpie Bug Swarm", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Bug lantern"}, org.rsbot.script.methods.Equipment.SHIELD))),
        HELLHOUND("Hellhound", new Location(new RSTile(0, 0), 0)),
        HILL_GIANT("Hill giant", new Location(new RSTile(0, 0), 0)),
        ICEFIEND("Icefiend", new Location(new RSTile(0, 0), 0)),
        ICE_GIANT("Ice giant", new Location(new RSTile(0, 0), 0)),
        ICE_WARRIOR("Ice warrior", new Location(new RSTile(0, 0), 0)),
        INFERNAL_MAGE("Infernal mage", new Location(new RSTile(0, 0), 0)),
        IRON_DRAGON("Iron dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        JELLY("Jelly", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Antipoison (1)", "Antipoison (2)", "Antipoison (3)", "Antipoison (4)"}))),
        JUNGLE_HORROR("Jungle horror", new Location(new RSTile(0, 0), 0)),
        KALPHITES("Kalphite worker", new Location(new RSTile(0, 0), 0)),
        KILLERWATT("Killerwatt", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Insulated boots"}, org.rsbot.script.methods.Equipment.FEET))),
        KURASK("Kurask", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Leaf-bladed sword", "Leaf-bladed spear"}, org.rsbot.script.methods.Equipment.WEAPON))),
        LESSER_DEMON("Lesser demon", new Location(new RSTile(0, 0), 0)),
        MITHRIL_DRAGON("Mithril dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        MINOTAUR("Minotaur", new Location(new RSTile(0, 0), 0)),
        MOGRE("Mogre", new Location(new RSTile(0, 0), 0)),
        //Need to complete a 'mini quest'
        MOLANISK("Molanisk", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Slayer bell"}), new Starter(new String[]{"Slayer bell"}))),
        MONKEY("Monkey", new Location(new RSTile(0, 0), 0), new Requirements(new CombatStyle(Style.RANGE))),
        MOSS_GIANT("Moss giant", new Location(new RSTile(0, 0), 0)),
        MUTATED_ZYGOMITE("Mutated zygomite", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Fungicide spray"}), new Finisher(new String[]{"Fungicide spray"}))),
        NECHRYAEL("Nechryael", new Location(new RSTile(0, 0), 0)),
        //Needs nosepeg for walking past spectres...
        OGRE("Ogre", new Location(new RSTile(0, 0), 0)),
        OTHERWORLDLY_BEINGS("Otherworldly beings", new Location(new RSTile(0, 0), 0)),
        PYREFIEND("Pyrefiend", new Location(new RSTile(0, 0), 0)),
        ROCK_SLUG("Rock slug", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Bag of salt"}), new Finisher(new String[]{"Bag of salt"}))),
        SCABARITES(" ", new Location(new RSTile(0, 0), 0)),
        //Need more infomation...
        SCORPION("Scorpion", new Location(new RSTile(0, 0), 0)),
        SEA_SNAKE("Sea snake hatchlings", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Antipoison (1)", "Antipoison (2)", "Antipoison (3)", "Antipoison (4)"}))),
        SHADE("Shade", new Location(new RSTile(0, 0), 0)),
        SHADOW_WARRIOR("Shadow warrior", new Location(new RSTile(0, 0), 0)),
        SKELETAL_WYVERN("Skeletal wyvern", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Mind shield", "Elemental shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        SKELETON("Skeleton", new Location(new RSTile(0, 0), 0)),
        SPIDER("Spider", new Location(new RSTile(0, 0), 0)),
        SPIRITUAL_MAGES("Spiritual mage", new Location(new RSTile(0, 0), 0)),
        SPIRITUAL_RANGER("Spiritual ranger", new Location(new RSTile(0, 0), 0)),
        SPIRITUAL_WARRIOR("Spiritual warrior", new Location(new RSTile(0, 0), 0)),
        STEEL_DRAGON("Steel dragon", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Dragonfire shield", "Anti-dragon shield"}, org.rsbot.script.methods.Equipment.SHIELD))),
        SUPAH("Suquh", new Location(new RSTile(0, 0), 0)),
        TROLL("Troll", new Location(new RSTile(0, 0), 0)),
        TUROTH("Turoth", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Leaf-bladed sword", "Leaf-bladed spear"}, org.rsbot.script.methods.Equipment.WEAPON))),
        VAMPIRE("Vampire", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Blessed holy symbol"}, org.rsbot.script.methods.Equipment.NECK))),
        WALL_BEAST("Wall beast", new Location(new RSTile(0, 0), 0), new Requirements(new Equipment(new String[]{"Slayer helmet", "Spiny helmet"}, org.rsbot.script.methods.Equipment.HELMET))),
        WARPED_TERRORBIRD("Warped terrorbird", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Crystal chime"}))),
        WARPED_TORTOISE("Warped tortoise", new Location(new RSTile(0, 0), 0), new Requirements(new Item(new String[]{"Crystal chime"}))),
        WATERFIEND("Waterfiend", new Location(new RSTile(0, 0), 0)),
        WEREWOLF(new String[]{"Werewolf", "Lev", "Svetlana", "Eduard", "Irina", "Boris"}, new Location(new RSTile(0, 0), 0)),
        WOLF(new String[]{"White wolf", "Big wolf", "Wolf"}, new Location(new RSTile(0, 0), 0)),
        ZOMBIE("Zombie", new Location(new RSTile(0, 0), 0)),
        UNKNOWN("", null);
        private String[] names;
        private Location location;
        private Requirements Requirements;

        Monsters(String[] names, Location location, Requirements Requirements) {
            this.names = names;
            this.location = location;
            this.Requirements = Requirements;
        }

        Monsters(String[] names, Location location) {
            this(names, location, null);
        }

        Monsters(String name, Location location) {
            this(new String[]{name}, location, null);
        }

        Monsters(String name, Location location, Requirements Requirements) {
            this(new String[]{name}, location, Requirements);
        }

        private Location getLocation() {
            return this.location;
        }

        private String[] getNames() {
            return this.names;
        }
    }

    private static class Requirements {
        List<Item> items = new ArrayList<Item>();
        Finisher finisher = Finisher();
        Starter starter = Starter();
        List<Equipment> equipments = new ArrayList<Equipment>();
        CombatStyle style = null;

        private Requirements(Item[] itemArray, Finisher finisher, Starter starter, Equipment[] equipmentArray, CombatStyle style) {
            this.items.addAll(Arrays.asList(itemArray));
            this.finisher = finisher;
            this.starter = starter;
            this.equipments.addAll(Arrays.asList(equipmentArray));
            this.style = style;
        }

        private Requirements(Item[] itemArray, Finisher finisher, Starter starter, Equipment[] equipmentArray) {
            this(itemArray, finisher, starter, equipmentArray, null);
        }

        private Requirements(Item[] itemArray, Finisher finisher, Starter starter) {
            this(itemArray, finisher, starter, null, null);
        }

        private Requirements(Item[] itemArray, Finisher finisher) {
            this(itemArray, finisher, null, null, null);
        }

        private Requirements(Item item, Finisher finisher) {
            this(new Item[]{item}, finisher, null, null, null);
        }

        private Requirements(Item[] itemArray, Starter starter) {
            this(itemArray, null, starter, null, null);
        }

        private Requirements(Item item, Starter starter) {
            this(new Item[]{item}, null, starter, null, null);
        }

        private Requirements(CombatStyle style) {
            this(null, null, null, null, style);
        }

        private Requirements(Equipment[] equipmentArray, CombatStyle style) {
            this(null, null, null, equipmentArray, style);
        }

        private Requirements(Equipment[] equipmentArray) {
            this(null, null, null, equipmentArray, null);
        }

        private Requirements(Equipment equipment) {
            this(null, null, null, new Equipment[]{equipment}, null);
        }

        private Requirements(Item[] itemArray) {
            this(itemArray, null, null, null, null);
        }

        private Requirements(Item item) {
            this(new Item[]{item}, null, null, null, null);
        }

        Item[] getItems() {
            Item[] itemArray = null;
            this.items.toArray(itemArray);
            return itemArray;
        }

        Finisher getFinisher() {
            return this.finisher;
        }

        Starter getStarter() {
            return this.starter;
        }

        Equipment[] getEquipment() {
            Equipment[] equipmentArray = null;
            this.finishers.toArray(equipmentArray);
            return equipmentArray;
        }

        CombatStyle getCombatStyle() {
            return this.style;
        }
    }

    private static class Item {
        private String[] names;
        private int amount;

        private Item(String[] names, int amount) {
            this.names = names;
            this.amount = amount;
        }

        private Item(String[] names) {
            this(names, 1);
        }

        private Item(String name) {
            this(new String[]{name}, 1);
        }

        private Item(String name, int amount) {
            this(new String[]{name}, amount);
        }

        private int getAmount() {
            return this.amount;
        }

        private String[] getNames() {
            return this.names;
        }
    }

    private static class Finisher {
        private String[] names;
        private int amount;

        private Finisher(String[] names, int amount) {
            this.names = names;
            this.amount = amount;
        }

        private Finisher(String[] names) {
            this(names, 1);
        }

        private Finisher(String name) {
            this(new String[]{name}, 1);
        }

        private Finisher(String name, int amount) {
            this(new String[]{name}, amount);
        }

        private Finisher(String[] names, boolean full) {
            this(names, (full ? 28 : 1));
        }

        private Finisher(String name, boolean full) {
            this(new String[]{name}, (full ? 28 : 1));
        }

        private int getAmount() {
            return this.amount;
        }

        private String[] getNames() {
            return this.names;
        }
    }

    private static class Starter {
        private String[] names;
        private int amount;

        private Starter(String[] names, int amount) {
            this.names = names;
            this.amount = amount;
        }

        private Starter(String[] names) {
            this(names, 1);
        }

        private Starter(String name) {
            this(new String[]{name}, 1);
        }

        private Starter(String name, int amount) {
            this(new String[]{name}, amount);
        }

        private Starter(String[] names, boolean full) {
            this(names, (full ? 28 : 1));
        }

        private Starter(String name, boolean full) {
            this(new String[]{name}, (full ? 28 : 1));
        }

        private int getAmount() {
            return this.amount;
        }

        private String[] getNames() {
            return this.names;
        }
    }

    private static class Equipment {
        private String[] names;
        private int slot;

        private Equipment(String[] names, int slot) {
            this.names = names;
            this.slot = slot;
        }

        private Equipment(String name, int slot) {
            this(new String[]{name}, slot);
        }

        private String[] getNames() {
            return this.names;
        }

        private int getSlot() {
            return this.slot;
        }
    }

    private static class CombatStyle {
        Style style;

        private CombatStyle(Style style) {
            this.style = style;
        }
    }

    private static enum Style {
        MELEE,
        MAGIC,
        RANGE;
    }

    private static class Location {
        private RSTile tile;
        private int plane;

        private Location(RSTile tile, int plane) {
            this.tile = tile;
            this.plane = plane;
        }

        private RSTile getTile() {
            return this.tile;
        }

        private int getPlane() {
            return this.plane;
        }
    }

    private class Task {
        private Monsters monster;

        private Task(Monsters monster) {
            this.monster = monster;
        }

        private Requirements getRequirements() {
            return this.monster.Requirements;
        }

        private Monsters getMonster() {
            return this.monster;
        }

        private void setMonster(Monsters monster) {
            this.monster = monster;
        }
    }

    private int getKillsLeft() {
        return settings.getSetting(394);
    }

    private int inventSpace() {
        return (28 - inventory.getCount());
    }

    private int specialUsage() {
        int[] amountUsage = {10, 25, 33, 35, 45, 50, 55, 60, 80, 85, 100};
        String[][] weapons = {{"Rune thrownaxe", "Rod of ivandis"}, {"Dragon Dagger", "Dragon dagger (p)", "Dragon dagger (p+)", "Dragon dagger (p++)", "Dragon Mace", "Dragon Spear", "Dragon longsword", "Rune claws"}, {"Dragon Halberd"}, {"Magic Longbow"}, {"Magic Composite Bow"}, {"Dragon Claws", "Abyssal Whip", "Granite Maul", "Darklight", "Barrelchest Anchor", "Armadyl Godsword"}, {"Magic Shortbow"}, {"Dragon Scimitar", "Dragon 2H Sword", "Zamorak Godsword", "Korasi's sword"}, {"Dorgeshuun Crossbow", "Bone Dagger", "Bone Dagger (p+)", "Bone Dagger (p++)"}, {"Brine Sabre"}, {"Bandos Godsword", "Dragon Battleaxe", "Dragon Hatchet", "Seercull Bow", "Excalibur", "Enhanced excalibur", "Ancient Mace", "Saradomin sword"}};
        String str = equipment.getItem(org.rsbot.script.methods.Equipment.WEAPON).getName();
        str = str.substring(str.indexOf(">") + 1);
        for (int i = 0; i < weapons.length; i++) {
            for (int j = 0; j < weapons[i].length; j++) {
                if (weapons[i][j].equalsIgnoreCase(str)) {
                    return amountUsage[i];
                }
            }
        }
        return -1;
    }

    private boolean isInInvent(Item items) {
        for (RSItem item : inventory.getItems()) {
            for (String name : items.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInBank(Item items) {
        for (RSItem item : bank.getItems()) {
            for (String name : items.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInInvent(Equipment equip) {
        for (RSItem item : inventory.getItems()) {
            for (String name : equip.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEquiped(Equipment equip) {
        for (RSItem item : equipment.getItems()) {
            for (String name : equip.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInBank(Equipment equip) {
        for (RSItem item : bank.getItems()) {
            for (String name : equip.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String willRemove(Equipment equip) {
        return equipment.getItem(equip.slot).getName();
    }

    private void equip(Equipment equip) {
        for (RSItem item : inventory.getItems()) {
            for (String name : equip.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    item.doClick(true);
                    return;
                }
            }
        }
    }

    private boolean isFullyEquiped(Requirements req) {
        for (Equipment e : req.getEquipment()) {
            if (!isEquiped(e)) {
                if (isInInvent(e)) {
                    for (Equipment r : req.getEquipment()) {
                        for (String name : r.getNames()) {
                            if (willRemove(e).equals(name)) {
                                return false;
                            }
                        }
                    }
                    equip(e);
                    if (!isEquiped(e)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean inventReady(Requirements req) {
        for (Item i : req.getItems()) {
            if (!isInInvent(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInInvent(Finisher fin) {
        for (RSItem item : inventory.getItems()) {
            for (String name : fin.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInBank(Finisher fin) {
        for (RSItem item : bank.getItems()) {
            for (String name : fin.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInInvent(Starter start) {
        for (RSItem item : inventory.getItems()) {
            for (String name : start.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInBank(Starter start) {
        for (RSItem item : bank.getItems()) {
            for (String name : start.getNames()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean use(Starter start, RSNPC monster) {
		for (String s : start.getNames()) {
			for (RSItem inventItem : inventory.getItems()) {
				if (s.equalsIgnoreCase(inventItem.getName())) {
					if (inventory.selectItem(inventItem.getID())) {
						if (monster != null) {
							if (!monster.isOnScreen()) {
								camera.turnTo(monster);
							}
							if (monster.isOnScreen()) {
								return monster.doAction("Use");
							}
						}
					}
				}
			}
		}
        return false;
    }

    private boolean use(Finisher finisher, RSNPC monster) {
		for (String s : finisher.getNames()) {
			for (RSItem inventItem : inventory.getItems()) {
				if (s.equalsIgnoreCase(inventItem.getName())) {
					if (inventory.selectItem(inventItem.getID())) {
						if (monster != null) {
							if (!monster.isOnScreen()) {
								camera.turnTo(monster);
							}
							if (monster.isOnScreen()) {
								return monster.doAction("Use");
							}
						}
					}
				}
			}
		}
        return false;
    }

    @Override
    public int loop() {
        return 0;
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Image closed = getImage("http://img860.imageshack.us/img860/5299/closedr.png");
	private final Image tabOne = getImage("http://img692.imageshack.us/img692/2836/gentab.png");
    private final Image tabTwo = getImage("http://img687.imageshack.us/img687/5461/exptab.png");
	private final Rectangle hideRect = new Rectangle(477, 336, 34, 37);
	private final Rectangle tabOneRect = new Rectangle(177, 335, 147, 37);
	private final Rectangle tabTwoRect = new Rectangle(327, 336, 148, 37);

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        if(tab == 3){
			g.drawImage(closed, 161, 293, null);
		}else{
			g.drawImage(tab == 1? tabOne: tabTwo, -1, 293, null);
		}
    }

    public void mouseClicked(MouseEvent e) {
        if(hideRect.contains(e.getPoint())){
             tab = 3;
        }else if(tabOneRect.contains(e.getPoint())){
             tab = 1;
        }else if(tabTwoRect.contains(e.getPoint())){
             tab = 2;
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
