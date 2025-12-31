package me.huanmeng.gui.example.shop;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.HGui;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.impl.GuiCustom;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Level 1: Main shop GUI showing all categories.
 *
 * Navigation: Main Shop → Category Browser
 */
public class MainShopGui extends HGui {

    public MainShopGui(Player player, boolean allowBack) {
        super(player, allowBack);
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(5);  // 5 rows
        gui.title("§6§l⚔ Advanced Shop System §6§l⚔");

        ShopManager shop = ShopManager.getInstance();

        // Category buttons in a grid layout
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        int index = 0;

        for (ShopCategory category : ShopCategory.values()) {
            if (index >= slots.length) break;

            int itemCount = shop.getCategoryItemCount(category);
            ItemStack item = createItem(
                category.getIcon(),
                "§e§l" + category.getDisplayName(),
                category.getDescription(),
                "",
                "§7Items available: §a" + itemCount,
                "",
                "§eClick to browse!"
            );

            gui.draw().set(Slot.of(slots[index++]), Button.of(item, click -> {
                // Navigate to category browser (Level 2)
                new CategoryBrowserGui(context.getPlayer(), true, category).open();
            }));
        }

        // Shop info
        ItemStack infoItem = createItem(
            Material.BOOK,
            "§b§lShop Information",
            "§7Welcome to the advanced shop!",
            "",
            "§eFeatures:",
            "§7• Browse by category",
            "§7• View detailed item information",
            "§7• Customize items with enchantments",
            "§7• Purchase wizard with confirmation",
            "§7• Full navigation stack support",
            "",
            "§aDeep navigation example:",
            "§7Main → Category → Items → Details",
            "§7  → Purchase → Enchantments → Summary"
        );
        gui.draw().set(Slot.of(40), Button.of(infoItem));

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
