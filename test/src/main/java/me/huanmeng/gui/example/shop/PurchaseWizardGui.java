package me.huanmeng.gui.example.shop;

import me.huanmeng.gui.AbstractGui;
import me.huanmeng.gui.StackableGui;
import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.impl.GuiCustom;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodType;
import java.util.Arrays;

/**
 * Level 4: Purchase wizard for customizable items.
 *
 * Navigation: Main → Category → Details → [Purchase Wizard] → Enchantments → Summary
 *
 * Allows players to choose customization options.
 */
public class PurchaseWizardGui extends StackableGui {

    private final ShopItem.PurchaseConfig purchaseConfig;

    public PurchaseWizardGui(Player player, boolean allowBack, ShopItem.PurchaseConfig config) {
        super(player, allowBack);
        this.purchaseConfig = config;

        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, ShopItem.PurchaseConfig.class),
            (p, back) -> Arrays.asList(p, back, config)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(5);
        gui.title("§6Purchase Wizard: " + purchaseConfig.getItem().getName());

        // Display current configuration
        ItemStack configDisplay = createConfigDisplay();
        gui.draw().set(Slot.of(13), Button.of(configDisplay));

        // Option 1: Skip enchantments (buy as-is)
        ItemStack skipEnch = createItem(
            Material.PAPER,
            "§a§lBuy Without Enchantments",
            "§7Purchase the basic item",
            "",
            "§6Total: §e" + purchaseConfig.getTotalPrice() + " coins",
            "",
            "§eClick to proceed to checkout"
        );
        gui.draw().set(Slot.of(20), Button.ofPlayerClick(skipEnch, p -> {
            // Skip to summary (Level 6)
            new TransactionSummaryGui(context.getPlayer(), true, purchaseConfig).open();
        }));

        // Option 2: Add enchantments
        if (!purchaseConfig.getItem().getAvailableEnchantments().isEmpty()) {
            ItemStack addEnch = createItem(
                Material.ENCHANTED_BOOK,
                "§b§lCustomize with Enchantments",
                "§7Add magical enhancements",
                "",
                "§eAvailable: §a" + purchaseConfig.getItem().getAvailableEnchantments().size() + " enchantments",
                "",
                "§eClick to select enchantments"
            );
            gui.draw().set(Slot.of(24), Button.ofPlayerClick(addEnch, p -> {
                // Navigate to enchantment selector (Level 5)
                new EnchantmentSelectorGui(context.getPlayer(), true, purchaseConfig).open();
            }));
        }

        // Add custom name option (placeholder)
        ItemStack customName = createItem(
            Material.NAME_TAG,
            "§6§lCustom Name",
            "§7Give your item a custom name",
            "",
            "§8(Not implemented in demo)"
        );
        gui.draw().set(Slot.of(22), Button.of(customName));

        // Wizard info
        ItemStack wizardInfo = createItem(
            Material.BOOK,
            "§e§lPurchase Wizard",
            "§7Step 1: Choose Options",
            "",
            "§7You can:",
            "§a• Buy the item as-is",
            "§b• Add enchantments",
            "§6• Add custom name",
            "",
            "§7After selection, you'll see",
            "§7a final confirmation screen"
        );
        gui.draw().set(Slot.of(40), Button.of(wizardInfo));

        // Back button
        ItemStack backItem = createItem(
            Material.ARROW,
            "§c§lBack to Item Details",
            "§7Return to previous screen"
        );
        gui.draw().set(Slot.of(36), Button.ofPlayerClick(backItem, p -> gui.back()));

        return gui;
    }

    private ItemStack createConfigDisplay() {
        ShopItem item = purchaseConfig.getItem();

        ItemStack display = new ItemStack(item.getMaterial());
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l" + item.getName());
            meta.setLore(Arrays.asList(
                "§7" + item.getDescription(),
                "",
                "§6═══ Current Configuration ═══",
                "§7Quantity: §e" + purchaseConfig.getQuantity(),
                "§7Base Price: §e" + item.getBasePrice() + " §7per item",
                "§7Enchantments: §e" + purchaseConfig.getSelectedEnchantments().size(),
                "",
                "§6Total Price: §e" + purchaseConfig.getTotalPrice() + " coins"
            ));
            display.setItemMeta(meta);
        }
        return display;
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
