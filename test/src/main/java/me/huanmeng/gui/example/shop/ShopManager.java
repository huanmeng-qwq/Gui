package me.huanmeng.gui.example.shop;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages shop inventory and provides item lookup.
 */
public class ShopManager {
    private static ShopManager instance;
    private final Map<String, ShopItem> items;
    private final Map<ShopCategory, List<ShopItem>> itemsByCategory;

    private ShopManager() {
        this.items = new HashMap<>();
        this.itemsByCategory = new EnumMap<>(ShopCategory.class);
        initializeShop();
    }

    public static ShopManager getInstance() {
        if (instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }

    private void initializeShop() {
        // Weapons & Combat
        addItem(new ShopItem("diamond_sword", "Diamond Sword", Material.DIAMOND_SWORD,
            "A powerful melee weapon", 500, ShopCategory.WEAPONS, 50, true)
            .addEnchantment(Enchantment.getByName("DAMAGE_ALL"), 5, 100) // Sharpness
            .addEnchantment(Enchantment.getByName("FIRE_ASPECT"), 2, 150)
            .addEnchantment(Enchantment.getByName("KNOCKBACK"), 2, 75)
            .addEnchantment(Enchantment.getByName("LOOT_BONUS_MOBS"), 3, 200)); // Looting

        addItem(new ShopItem("diamond_axe", "Diamond Axe", Material.DIAMOND_AXE,
            "Chop enemies and trees", 450, ShopCategory.WEAPONS, 40, true)
            .addEnchantment(Enchantment.getByName("DAMAGE_ALL"), 5, 100)
            .addEnchantment(Enchantment.getByName("DIG_SPEED"), 5, 80)); // Efficiency

        addItem(new ShopItem("bow", "Bow", Material.BOW,
            "Ranged weapon", 300, ShopCategory.WEAPONS, 100, true)
            .addEnchantment(Enchantment.getByName("ARROW_DAMAGE"), 5, 120) // Power
            .addEnchantment(Enchantment.getByName("ARROW_FIRE"), 1, 200) // Flame
            .addEnchantment(Enchantment.getByName("ARROW_INFINITE"), 1, 500) // Infinity
            .addEnchantment(Enchantment.getByName("ARROW_KNOCKBACK"), 2, 100)); // Punch

        addItem(new ShopItem("diamond_helmet", "Diamond Helmet", Material.DIAMOND_HELMET,
            "Head protection", 400, ShopCategory.WEAPONS, 30, true)
            .addEnchantment(Enchantment.getByName("PROTECTION_ENVIRONMENTAL"), 4, 150) // Protection
            .addEnchantment(Enchantment.getByName("OXYGEN"), 3, 100) // Respiration
            .addEnchantment(Enchantment.getByName("THORNS"), 3, 200));

        // Tools & Equipment
        addItem(new ShopItem("diamond_pickaxe", "Diamond Pickaxe", Material.DIAMOND_PICKAXE,
            "Mine faster", 600, ShopCategory.TOOLS, 50, true)
            .addEnchantment(Enchantment.getByName("DIG_SPEED"), 5, 100) // Efficiency
            .addEnchantment(Enchantment.getByName("LOOT_BONUS_BLOCKS"), 3, 300) // Fortune
            .addEnchantment(Enchantment.getByName("SILK_TOUCH"), 1, 400));

        addItem(new ShopItem("diamond_shovel", "Diamond Shovel", Material.DIAMOND_SHOVEL,
            "Dig dirt and gravel", 350, ShopCategory.TOOLS, 60, true)
            .addEnchantment(Enchantment.getByName("DIG_SPEED"), 5, 80)
            .addEnchantment(Enchantment.getByName("SILK_TOUCH"), 1, 300));

        addItem(new ShopItem("shears", "Shears", Material.SHEARS,
            "Collect wool and leaves", 50, ShopCategory.TOOLS, 100, false));

        // Blocks
        addItem(new ShopItem("stone_bricks", "Stone Bricks", Material.STONE_BRICKS,
            "Building material", 10, ShopCategory.BLOCKS, 1000, false));

        addItem(new ShopItem("oak_planks", "Oak Planks", Material.OAK_PLANKS,
            "Wooden planks", 5, ShopCategory.BLOCKS, 2000, false));

        addItem(new ShopItem("glass", "Glass", Material.GLASS,
            "Transparent block", 15, ShopCategory.BLOCKS, 500, false));

        // Potions & Brewing
        addItem(new ShopItem("healing_potion", "Potion of Healing", Material.POTION,
            "Restores health", 100, ShopCategory.POTIONS, 200, false));

        addItem(new ShopItem("strength_potion", "Potion of Strength", Material.POTION,
            "Increases damage", 150, ShopCategory.POTIONS, 150, false));

        addItem(new ShopItem("brewing_stand", "Brewing Stand", Material.BREWING_STAND,
            "Create potions", 250, ShopCategory.POTIONS, 50, false));

        // Food & Farming
        addItem(new ShopItem("steak", "Cooked Beef", Material.COOKED_BEEF,
            "Restores 8 hunger", 20, ShopCategory.FOOD, 500, false));

        addItem(new ShopItem("golden_apple", "Golden Apple", Material.GOLDEN_APPLE,
            "Regeneration and absorption", 200, ShopCategory.FOOD, 100, false));

        addItem(new ShopItem("wheat_seeds", "Wheat Seeds", Material.WHEAT_SEEDS,
            "Grow wheat", 5, ShopCategory.FOOD, 1000, false));

        // Redstone & Tech
        addItem(new ShopItem("redstone", "Redstone Dust", Material.REDSTONE,
            "Power component", 25, ShopCategory.REDSTONE, 800, false));

        addItem(new ShopItem("piston", "Piston", Material.PISTON,
            "Push blocks", 50, ShopCategory.REDSTONE, 200, false));

        addItem(new ShopItem("hopper", "Hopper", Material.HOPPER,
            "Item transportation", 75, ShopCategory.REDSTONE, 150, false));

        // Decorations
        addItem(new ShopItem("painting", "Painting", Material.PAINTING,
            "Wall decoration", 30, ShopCategory.DECORATIONS, 100, false));

        addItem(new ShopItem("flower_pot", "Flower Pot", Material.FLOWER_POT,
            "Hold plants", 20, ShopCategory.DECORATIONS, 200, false));
    }

    private void addItem(ShopItem item) {
        items.put(item.getId(), item);
        itemsByCategory.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
    }

    public ShopItem getItem(String id) {
        return items.get(id);
    }

    public List<ShopItem> getItemsByCategory(ShopCategory category) {
        return itemsByCategory.getOrDefault(category, Collections.emptyList());
    }

    public List<ShopItem> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public int getCategoryItemCount(ShopCategory category) {
        return getItemsByCategory(category).size();
    }
}
