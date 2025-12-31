package me.huanmeng.gui.example.navigation;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.HGui;
import me.huanmeng.gui.gui.impl.GuiCustom;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.List;

/**
 * Category GUI - displays items in a specific category.
 *
 * This demonstrates:
 * - Passing parameters to GUI constructors
 * - Creating dynamic content based on category
 * - Multi-level navigation (Main → Category → Detail)
 */
public class CategoryGui extends HGui {

    private final String categoryName;

    public CategoryGui(Player player, boolean allowBack, String categoryName) {
        super(player, allowBack);
        this.categoryName = categoryName;

        // Set custom constructor for proper back navigation
        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, String.class),
            (p, back) -> Arrays.asList(p, back, categoryName)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(4);  // 4 rows
        gui.title("§6§l" + categoryName + " Category");

        // Get items for this category
        List<ItemData> items = getItemsForCategory(categoryName);

        // Display items
        for (int i = 0; i < items.size() && i < 21; i++) {
            ItemData itemData = items.get(i);
            ItemStack displayItem = createItem(
                itemData.material,
                "§e" + itemData.name,
                "§7" + itemData.description,
                "",
                "§aClick to view details"
            );

            gui.draw().set(Slot.of(10 + i), Button.of(displayItem, click -> {
                // Open detail page for this item
                new ItemDetailGui(context.getPlayer(), true, itemData).open();
            }));
        }

        // Back button (optional - HGui provides automatic back functionality)
        ItemStack backItem = createItem(
            Material.ARROW,
            "§c§lBack to Main Menu",
            "§7Click to return"
        );
        gui.draw().set(Slot.of(31), Button.of(backItem, click -> {
            // Use the built-in back functionality
            gui.back();
        }));

        return gui;
    }

    /**
     * Get items for a specific category.
     */
    private List<ItemData> getItemsForCategory(String category) {
        switch (category) {
            case "Weapons":
                return Arrays.asList(
                    new ItemData(Material.DIAMOND_SWORD, "Diamond Sword", "A powerful melee weapon"),
                    new ItemData(Material.BOW, "Bow", "Ranged weapon for distance attacks"),
                    new ItemData(Material.TRIDENT, "Trident", "Versatile throwing weapon")
                );
            case "Tools":
                return Arrays.asList(
                    new ItemData(Material.DIAMOND_PICKAXE, "Diamond Pickaxe", "Mine stone and ores"),
                    new ItemData(Material.DIAMOND_AXE, "Diamond Axe", "Chop wood efficiently"),
                    new ItemData(Material.DIAMOND_SHOVEL, "Diamond Shovel", "Dig dirt and gravel")
                );
            case "Food":
                return Arrays.asList(
                    new ItemData(Material.COOKED_BEEF, "Steak", "Restores 8 hunger points"),
                    new ItemData(Material.GOLDEN_APPLE, "Golden Apple", "Provides regeneration"),
                    new ItemData(Material.CAKE, "Cake", "Delicious dessert")
                );
            default:
                return Arrays.asList();
        }
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Simple data class for items.
     */
    public static class ItemData {
        public final Material material;
        public final String name;
        public final String description;

        public ItemData(Material material, String name, String description) {
            this.material = material;
            this.name = name;
            this.description = description;
        }
    }
}
