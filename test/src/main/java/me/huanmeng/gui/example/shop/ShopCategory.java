package me.huanmeng.gui.example.shop;

import org.bukkit.Material;

/**
 * Shop categories with display information.
 */
public enum ShopCategory {
    WEAPONS("Weapons & Combat", Material.DIAMOND_SWORD, "§cWeapons, armor, and combat items"),
    TOOLS("Tools & Equipment", Material.DIAMOND_PICKAXE, "§bTools for mining, farming, and more"),
    BLOCKS("Building Blocks", Material.BRICKS, "§eBlocks for construction"),
    POTIONS("Potions & Brewing", Material.POTION, "§dPotions, ingredients, and brewing supplies"),
    FOOD("Food & Farming", Material.BREAD, "§aFood items and farming supplies"),
    REDSTONE("Redstone & Tech", Material.REDSTONE, "§cRedstone components and mechanisms"),
    DECORATIONS("Decorations", Material.PAINTING, "§6Decorative items and furniture");

    private final String displayName;
    private final Material icon;
    private final String description;

    ShopCategory(String displayName, Material icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }
    public String getDescription() { return description; }
}
