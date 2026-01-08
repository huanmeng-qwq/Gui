package me.huanmeng.gui.example.navigation;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.AbstractGui;
import me.huanmeng.gui.HGui;
import me.huanmeng.gui.impl.GuiCustom;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Main menu GUI - the entry point of the example navigation system.
 *
 * This demonstrates:
 * - Creating a simple custom GUI
 * - Opening child GUIs with back navigation enabled
 * - Button click handling
 */
public class MainMenuGui extends HGui {

    public MainMenuGui(Player player, boolean allowBack) {
        super(player, allowBack);
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(3);  // 3 rows
        gui.title("§6§lMain Menu §7(Example)");

        // Weapons category button
        ItemStack weaponsItem = createItem(
            Material.DIAMOND_SWORD,
            "§e§lWeapons Category",
            "§7Click to browse weapons",
            "§aSupports back navigation!"
        );
        gui.draw().set(Slot.of(11), Button.of(weaponsItem, click -> {
            // Open weapons category with back navigation enabled
            new CategoryGui(context.getPlayer(), true, "Weapons").open();
        }));

        // Tools category button
        ItemStack toolsItem = createItem(
            Material.DIAMOND_PICKAXE,
            "§e§lTools Category",
            "§7Click to browse tools",
            "§aSupports back navigation!"
        );
        gui.draw().set(Slot.of(13), Button.of(toolsItem, click -> {
            // Open tools category with back navigation enabled
            new CategoryGui(context.getPlayer(), true, "Tools").open();
        }));

        // Food category button
        ItemStack foodItem = createItem(
            Material.COOKED_BEEF,
            "§e§lFood Category",
            "§7Click to browse food items",
            "§aSupports back navigation!"
        );
        gui.draw().set(Slot.of(15), Button.of(foodItem, click -> {
            // Open food category with back navigation enabled
            new CategoryGui(context.getPlayer(), true, "Food").open();
        }));

        // Info button
        ItemStack infoItem = createItem(
            Material.BOOK,
            "§b§lNavigation Info",
            "§7This example demonstrates the",
            "§7refactored back navigation system.",
            "",
            "§eHow it works:",
            "§7• Each GUI is pushed onto a stack",
            "§7• Back button pops the previous GUI",
            "§7• No memory leaks or null pointers!",
            "",
            "§aTry navigating through menus!"
        );
        gui.draw().set(Slot.of(22), Button.of(infoItem));

        return gui;
    }

    /**
     * Helper method to create items with name and lore.
     */
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
}
