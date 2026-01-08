package me.huanmeng.gui.example.shop;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.HGui;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.impl.GuiCustom;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Level 6: Transaction summary and final confirmation.
 *
 * Navigation: Main → Category → Details → Wizard → Enchantments → [Summary]
 *
 * The deepest point in the navigation tree (6 levels).
 * Demonstrates complete stack-based navigation from start to finish.
 */
public class TransactionSummaryGui extends HGui {

    private final ShopItem.PurchaseConfig purchaseConfig;

    public TransactionSummaryGui(Player player, boolean allowBack, ShopItem.PurchaseConfig config) {
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
        gui.title("§6§lTransaction Summary");

        // Display final item preview
        ItemStack finalItem = createFinalItemPreview();
        gui.draw().set(Slot.of(13), Button.of(finalItem));

        // Price breakdown
        ItemStack priceBreakdown = createPriceBreakdown();
        gui.draw().set(Slot.of(29), Button.of(priceBreakdown));

        // Purchase confirmation button
        ItemStack confirmButton = createItem(
            Material.EMERALD_BLOCK,
            "§a§l✔ CONFIRM PURCHASE",
            "§7Complete the transaction",
            "",
            "§6Total: §e" + purchaseConfig.getTotalPrice() + " coins",
            "",
            "§aClick to buy!"
        );
        gui.draw().set(Slot.of(32), Button.ofPlayerClick(confirmButton, p -> {
            // Simulate purchase
            p.sendMessage("§a§l✔ Purchase successful!");
            p.sendMessage("§7You bought §e" + purchaseConfig.getQuantity() +
                "x " + purchaseConfig.getItem().getName() + " §7for §6" +
                purchaseConfig.getTotalPrice() + " coins§7!");

            if (!purchaseConfig.getSelectedEnchantments().isEmpty()) {
                p.sendMessage("§b§lEnchantments:");
                for (Map.Entry<Enchantment, Integer> entry : purchaseConfig.getSelectedEnchantments().entrySet()) {
                    p.sendMessage("§7• " + entry.getKey().getName() + " " + entry.getValue());
                }
            }

            gui.close();
        }));

        // Cancel button
        ItemStack cancelButton = createItem(
            Material.REDSTONE_BLOCK,
            "§c§l✖ CANCEL",
            "§7Go back to modify",
            "",
            "§cClick to cancel purchase"
        );
        gui.draw().set(Slot.of(33), Button.ofPlayerClick(cancelButton, p -> gui.back()));

        // Navigation depth indicator
        ItemStack depthIndicator = createItem(
            Material.COMPASS,
            "§e§lNavigation Depth: Level 6",
            "§7You are at the deepest level!",
            "",
            "§7Navigation path:",
            "§e1. Main Shop",
            "§e2. Category: " + purchaseConfig.getItem().getCategory().getDisplayName(),
            "§e3. Item Details",
            "§e4. Purchase Wizard",
            "§e5. Enchantment Selector",
            "§e6. Transaction Summary §a← You are here",
            "",
            "§7Click back §c" + 6 + " times §7to return to main shop!",
            "",
            "§aStack navigation works perfectly!"
        );
        gui.draw().set(Slot.of(40), Button.of(depthIndicator));

        // Player balance (simulated)
        ItemStack balanceItem = createItem(
            Material.GOLD_INGOT,
            "§6§lYour Balance",
            "§7Current: §e10000 coins §8(demo)",
            "§7After purchase: §e" + (10000 - purchaseConfig.getTotalPrice()) + " coins",
            "",
            purchaseConfig.getTotalPrice() <= 10000 ? "§a✔ Sufficient funds" : "§c✖ Insufficient funds"
        );
        gui.draw().set(Slot.of(8), Button.of(balanceItem));

        return gui;
    }

    private ItemStack createFinalItemPreview() {
        ShopItem item = purchaseConfig.getItem();
        List<String> lore = new ArrayList<>();

        lore.add("§7" + item.getDescription());
        lore.add("");
        lore.add("§6═══ Purchase Details ═══");
        lore.add("§7Quantity: §e" + purchaseConfig.getQuantity());
        lore.add("§7Unit Price: §e" + item.getBasePrice() + " coins");

        if (!purchaseConfig.getSelectedEnchantments().isEmpty()) {
            lore.add("");
            lore.add("§b═══ Enchantments ═══");
            for (Map.Entry<Enchantment, Integer> entry : purchaseConfig.getSelectedEnchantments().entrySet()) {
                lore.add("§7• " + entry.getKey().getName() + " " + entry.getValue());
            }
        }

        lore.add("");
        lore.add("§6§lTotal: §e" + purchaseConfig.getTotalPrice() + " coins");

        return createItem(item.getMaterial(), "§e§l" + item.getName(), lore.toArray(new String[0]));
    }

    private ItemStack createPriceBreakdown() {
        ShopItem item = purchaseConfig.getItem();
        List<String> lore = new ArrayList<>();

        int baseTotal = item.getBasePrice() * purchaseConfig.getQuantity();
        lore.add("§7Base Price:");
        lore.add("§e  " + item.getBasePrice() + " §7x §e" + purchaseConfig.getQuantity() + " §7= §e" + baseTotal + " coins");

        if (!purchaseConfig.getSelectedEnchantments().isEmpty()) {
            lore.add("");
            lore.add("§7Enchantment Costs:");
            int enchTotal = 0;
            for (Map.Entry<Enchantment, Integer> entry : purchaseConfig.getSelectedEnchantments().entrySet()) {
                Enchantment ench = entry.getKey();
                int level = entry.getValue();
                int pricePerLevel = item.getAvailableEnchantments().stream()
                    .filter(opt -> opt.getEnchantment().equals(ench))
                    .findFirst()
                    .map(ShopItem.EnchantmentOption::getPricePerLevel)
                    .orElse(0);
                int cost = pricePerLevel * level * purchaseConfig.getQuantity();
                enchTotal += cost;
                lore.add("§b  " + ench.getName() + " " + level + " §7= §e" + cost + " coins");
            }
            lore.add("§7Enchantment subtotal: §e" + enchTotal + " coins");
        }

        lore.add("");
        lore.add("§6═══════════════");
        lore.add("§6§lGRAND TOTAL: §e" + purchaseConfig.getTotalPrice() + " coins");

        return createItem(Material.PAPER, "§6§lPrice Breakdown", lore.toArray(new String[0]));
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
