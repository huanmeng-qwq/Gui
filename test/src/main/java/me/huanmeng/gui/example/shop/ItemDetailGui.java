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

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Level 3: Item details with purchase options.
 *
 * Navigation: Main Shop → Category → [Item Details] → Purchase Wizard
 */
public class ItemDetailGui extends HGui {

    private final ShopItem item;

    public ItemDetailGui(Player player, boolean allowBack, ShopItem item) {
        super(player, allowBack);
        this.item = item;

        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, ShopItem.class),
            (p, back) -> Arrays.asList(p, back, item)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(5);
        gui.title("§6Item Details: " + item.getName());

        // Display item
        ItemStack displayItem = createDetailedItem();
        gui.draw().set(Slot.of(13), Button.of(displayItem));

        // Quick purchase buttons (different quantities)
        ItemStack buy1 = createItem(Material.GOLD_NUGGET, "§aBuy 1", "§7Price: §e" + item.getBasePrice() + " coins");
        gui.draw().set(Slot.of(20), Button.of(buy1, click -> {
            ShopItem.PurchaseConfig config = new ShopItem.PurchaseConfig(item, 1);
            if (item.isCustomizable()) {
                // Navigate to purchase wizard (Level 4)
                new PurchaseWizardGui(context.getPlayer(), true, config).open();
            } else {
                // Skip to summary (Level 6)
                new TransactionSummaryGui(context.getPlayer(), true, config).open();
            }
        }));

        ItemStack buy4 = createItem(Material.GOLD_INGOT, "§aBuy 4", "§7Price: §e" + (item.getBasePrice() * 4) + " coins");
        gui.draw().set(Slot.of(22), Button.of(buy4, click -> {
            ShopItem.PurchaseConfig config = new ShopItem.PurchaseConfig(item, 4);
            if (item.isCustomizable()) {
                new PurchaseWizardGui(context.getPlayer(), true, config).open();
            } else {
                new TransactionSummaryGui(context.getPlayer(), true, config).open();
            }
        }));

        ItemStack buy16 = createItem(Material.GOLD_BLOCK, "§aBuy 16", "§7Price: §e" + (item.getBasePrice() * 16) + " coins");
        gui.draw().set(Slot.of(24), Button.of(buy16, click -> {
            ShopItem.PurchaseConfig config = new ShopItem.PurchaseConfig(item, 16);
            if (item.isCustomizable()) {
                new PurchaseWizardGui(context.getPlayer(), true, config).open();
            } else {
                new TransactionSummaryGui(context.getPlayer(), true, config).open();
            }
        }));

        // Custom amount
        ItemStack customAmount = createItem(
            Material.ANVIL,
            "§6Custom Amount",
            "§7Enter amount in chat",
            "§8(Not implemented in demo)"
        );
        gui.draw().set(Slot.of(31), Button.of(customAmount));

        // Enchantment preview (if customizable)
        if (item.isCustomizable() && !item.getAvailableEnchantments().isEmpty()) {
            ItemStack enchInfo = createItem(
                Material.ENCHANTED_BOOK,
                "§b§lAvailable Enchantments",
                "§7This item can be customized!",
                "",
                "§eEnchantments: §a" + item.getAvailableEnchantments().size(),
                "",
                "§7Purchase to customize"
            );
            gui.draw().set(Slot.of(15), Button.of(enchInfo));
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§c§lBack to Category", "§7Return to items list");
        gui.draw().set(Slot.of(36), Button.of(backItem, click -> gui.back()));

        // Statistics/Info
        ItemStack statsItem = createItem(
            Material.PAPER,
            "§e§lItem Statistics",
            "§7Category: §f" + item.getCategory().getDisplayName(),
            "§7Stock: §a" + item.getStock(),
            "§7Customizable: " + (item.isCustomizable() ? "§aYes" : "§cNo"),
            "",
            "§7Item ID: §8" + item.getId()
        );
        gui.draw().set(Slot.of(44), Button.of(statsItem));

        return gui;
    }

    private ItemStack createDetailedItem() {
        List<String> lore = new ArrayList<>();
        lore.add("§7" + item.getDescription());
        lore.add("");
        lore.add("§6═══ Pricing ═══");
        lore.add("§7Base Price: §e" + item.getBasePrice() + " coins");

        if (!item.getAvailableEnchantments().isEmpty()) {
            lore.add("");
            lore.add("§b═══ Enchantments ═══");
            for (ShopItem.EnchantmentOption opt : item.getAvailableEnchantments()) {
                lore.add("§7• " + opt.getEnchantment().getName() + " (I-" + opt.getMaxLevel() + ")");
                lore.add("  §6+" + opt.getPricePerLevel() + " coins/level");
            }
        }

        lore.add("");
        lore.add("§a═══ Availability ═══");
        lore.add("§7Stock: §a" + item.getStock() + " available");
        lore.add("§7Category: §f" + item.getCategory().getDisplayName());

        return createItem(item.getMaterial(), "§e§l" + item.getName(), lore.toArray(new String[0]));
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
