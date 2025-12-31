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

/**
 * Item detail GUI - shows detailed information about a specific item.
 *
 * This demonstrates:
 * - Third level of navigation (Main → Category → Detail)
 * - Complete navigation chain with proper back functionality
 * - Stack-based navigation working correctly through all levels
 */
public class ItemDetailGui extends HGui {

    private final CategoryGui.ItemData itemData;

    public ItemDetailGui(Player player, boolean allowBack, CategoryGui.ItemData itemData) {
        super(player, allowBack);
        this.itemData = itemData;

        // Set custom constructor for proper back navigation
        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, CategoryGui.ItemData.class),
            (p, back) -> Arrays.asList(p, back, itemData)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(3);  // 3 rows
        gui.title("§6§l" + itemData.name + " §7(Details)");

        // Display the item in the center
        ItemStack displayItem = new ItemStack(itemData.material);
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l" + itemData.name);
            meta.setLore(Arrays.asList(
                "§7" + itemData.description,
                "",
                "§6Item Type: §f" + itemData.material.name(),
                "§6Stack Size: §f" + displayItem.getMaxStackSize(),
                "",
                "§7This is a detailed view of the item.",
                "§7You can navigate back through the menus",
                "§7using the back button below."
            ));
            displayItem.setItemMeta(meta);
        }
        gui.draw().set(Slot.of(13), Button.of(displayItem));

        // Info about navigation
        ItemStack navInfo = createItem(
            Material.COMPASS,
            "§b§lNavigation Stack Demo",
            "§7Current navigation path:",
            "§eMain Menu §7→ §eCategory §7→ §eItem Detail",
            "",
            "§7Click back to return to category",
            "§7Click back again to return to main menu",
            "§7Click back once more to close the GUI",
            "",
            "§aThe stack ensures proper navigation!"
        );
        gui.draw().set(Slot.of(11), Button.of(navInfo));

        // Purchase button (example action)
        ItemStack purchaseItem = createItem(
            Material.EMERALD,
            "§a§lPurchase Item",
            "§7Click to purchase this item",
            "§6Price: §f100 coins"
        );
        gui.draw().set(Slot.of(15), Button.of(purchaseItem, click -> {
            context.getPlayer().sendMessage("§aPurchased " + itemData.name + "!");
            gui.close();
        }));

        // Back button
        ItemStack backItem = createItem(
            Material.ARROW,
            "§c§lBack to Category",
            "§7Click to return"
        );
        gui.draw().set(Slot.of(22), Button.of(backItem, click -> {
            gui.back();
        }));

        return gui;
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
}
