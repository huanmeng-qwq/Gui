package me.huanmeng.gui.example.shop;

import me.huanmeng.gui.AbstractGui;
import me.huanmeng.gui.StackableGui;
import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.impl.GuiPage;
import me.huanmeng.gui.impl.page.PageSettings;
import me.huanmeng.gui.slot.Slot;
import me.huanmeng.gui.slot.Slots;
import me.huanmeng.gui.slot.impl.slots.PatternSlots;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Level 2: Category browser with pagination.
 *
 * Navigation: Main Shop → [Category Browser] → Item Details
 *
 * Demonstrates:
 * - Paginated GUI with back navigation
 * - Custom constructor parameters
 * - State preservation across navigation
 */
public class CategoryBrowserGui extends StackableGui {

    private final ShopCategory category;

    public CategoryBrowserGui(Player player, boolean allowBack, ShopCategory category) {
        super(player, allowBack);
        this.category = category;

        // Set custom constructor for back navigation
        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, ShopCategory.class),
            (p, back) -> Arrays.asList(p, back, category)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        ShopManager shop = ShopManager.getInstance();
        List<ShopItem> items = shop.getItemsByCategory(category);

        // Convert items to buttons
        List<Button> itemButtons = new ArrayList<>();
        for (ShopItem item : items) {
            ItemStack displayItem = createItemDisplay(item);
            itemButtons.add(Button.ofPlayerClick(displayItem, p -> {
                // Navigate to item details (Level 3)
                new ItemDetailGui(p, true, item).open();
            }));
        }

        // Define page slots (3 rows of items)
        Slots pageSlots = new PatternSlots(new String[]{
            "OOOOOOOOO",
            "OOOOOOOOO",
            "OOOOOOOOO",
            "XXXXXXXXX",
            "XXXXXXXXX"
        }, 'O');

        // Create paginated GUI
        GuiPage gui = new GuiPage(context.getPlayer(), itemButtons, pageSlots);
        gui.title("§6" + category.getDisplayName() + " §7(Page %page%/%maxPage%)");

        // Configure page navigation
        gui.pageSetting(PageSettings.normal(gui));

        // Add back button
        ItemStack backItem = createItem(
            Material.ARROW,
            "§c§lBack to Categories",
            "§7Return to main shop"
        );
        gui.draw().set(Slot.of(36), Button.ofPlayerClick(backItem, p -> gui.back()));

        // Add category info
        ItemStack infoItem = createItem(
            category.getIcon(),
            "§e§l" + category.getDisplayName(),
            category.getDescription(),
            "",
            "§7Total items: §a" + items.size(),
            "§7Current page: §e" + (gui.page() + 1) + "/" + (gui.pagination().getMaxPage() + 1)
        );
        gui.draw().set(Slot.of(40), Button.of(infoItem));

        // Add sorting/filter info (placeholder)
        ItemStack filterItem = createItem(
            Material.HOPPER,
            "§b§lSort & Filter",
            "§7Sort by: §eName (A-Z)",
            "§7Filter: §aNone",
            "",
            "§8(Sorting not implemented in demo)"
        );
        gui.draw().set(Slot.of(44), Button.of(filterItem));

        return gui;
    }

    private ItemStack createItemDisplay(ShopItem item) {
        List<String> lore = new ArrayList<>();
        lore.add("§7" + item.getDescription());
        lore.add("");
        lore.add("§6Price: §e" + item.getBasePrice() + " coins");
        lore.add("§7Stock: §a" + item.getStock());

        if (!item.getAvailableEnchantments().isEmpty()) {
            lore.add("");
            lore.add("§b✦ Customizable");
            lore.add("§7Can add enchantments");
        }

        lore.add("");
        lore.add("§eClick to view details!");

        return createItem(item.getMaterial(), "§e" + item.getName(), lore.toArray(new String[0]));
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
