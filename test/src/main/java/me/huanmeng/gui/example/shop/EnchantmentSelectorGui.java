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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Level 5: Enchantment selector (deepest navigation level).
 *
 * Navigation: Main → Category → Details → Wizard → [Enchantments] → Summary
 *
 * Demonstrates:
 * - Complex state management
 * - Mutable configuration through navigation
 * - Deep nesting (5 levels)
 */
public class EnchantmentSelectorGui extends HGui {

    private final ShopItem.PurchaseConfig baseConfig;
    private final Map<Enchantment, Integer> selectedEnchantments;

    public EnchantmentSelectorGui(Player player, boolean allowBack, ShopItem.PurchaseConfig config) {
        super(player, allowBack);
        this.baseConfig = config;
        this.selectedEnchantments = new HashMap<>(config.getSelectedEnchantments());

        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, ShopItem.PurchaseConfig.class),
            (p, back) -> Arrays.asList(p, back, config)
        );
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(6);
        gui.title("§b§lSelect Enchantments");

        // Display available enchantments
        List<ShopItem.EnchantmentOption> options = baseConfig.getItem().getAvailableEnchantments();

        int[] enchSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < Math.min(options.size(), enchSlots.length); i++) {
            ShopItem.EnchantmentOption opt = options.get(i);
            int currentLevel = selectedEnchantments.getOrDefault(opt.getEnchantment(), 0);

            ItemStack enchItem = createEnchantmentItem(opt, currentLevel);
            int slot = enchSlots[i];

            gui.draw().set(Slot.of(slot), Button.ofPlayerClick(enchItem, p -> {
                // Cycle through levels: 0 → 1 → 2 → ... → max → 0
                int newLevel = (currentLevel + 1) % (opt.getMaxLevel() + 1);

                if (newLevel == 0) {
                    selectedEnchantments.remove(opt.getEnchantment());
                } else {
                    selectedEnchantments.put(opt.getEnchantment(), newLevel);
                }

                // Refresh GUI to show new selection
                context.getPlayer().closeInventory();
                new EnchantmentSelectorGui(context.getPlayer(), true, baseConfig).open();
            }));
        }

        // Price summary
        int enchantmentCost = calculateEnchantmentCost();
        ItemStack priceItem = createItem(
            Material.GOLD_INGOT,
            "§6§lPrice Summary",
            "§7Base price: §e" + (baseConfig.getItem().getBasePrice() * baseConfig.getQuantity()) + " coins",
            "§7Enchantment cost: §e" + enchantmentCost + " coins",
            "",
            "§6Total: §e" + (baseConfig.getItem().getBasePrice() * baseConfig.getQuantity() + enchantmentCost) + " coins"
        );
        gui.draw().set(Slot.of(49), Button.of(priceItem));

        // Current selection display
        ItemStack selectionItem = createSelectionDisplay();
        gui.draw().set(Slot.of(4), Button.of(selectionItem));

        // Confirm button
        ItemStack confirmItem = createItem(
            Material.EMERALD,
            "§a§lConfirm Selection",
            "§7Proceed to checkout",
            "",
            "§aSelected: §e" + selectedEnchantments.size() + " enchantments",
            "",
            "§eClick to continue"
        );
        gui.draw().set(Slot.of(48), Button.ofPlayerClick(confirmItem, p -> {
            // Create new config with selected enchantments
            ShopItem.PurchaseConfig finalConfig = new ShopItem.PurchaseConfig(
                baseConfig.getItem(),
                baseConfig.getQuantity(),
                selectedEnchantments,
                null
            );

            // Navigate to summary (Level 6)
            new TransactionSummaryGui(context.getPlayer(), true, finalConfig).open();
        }));

        // Clear all button
        ItemStack clearItem = createItem(
            Material.BARRIER,
            "§c§lClear All",
            "§7Remove all enchantments"
        );
        gui.draw().set(Slot.of(45), Button.ofPlayerClick(clearItem, p -> {
            selectedEnchantments.clear();
            context.getPlayer().closeInventory();
            new EnchantmentSelectorGui(context.getPlayer(), true, baseConfig).open();
        }));

        // Back button
        ItemStack backItem = createItem(
            Material.ARROW,
            "§c§lBack to Wizard",
            "§7Return to purchase options"
        );
        gui.draw().set(Slot.of(46), Button.ofPlayerClick(backItem, p -> gui.back()));

        return gui;
    }

    private ItemStack createEnchantmentItem(ShopItem.EnchantmentOption opt, int currentLevel) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Max Level: §e" + opt.getMaxLevel());
        lore.add("§7Cost: §6" + opt.getPricePerLevel() + " coins §7per level");
        lore.add("");

        if (currentLevel > 0) {
            lore.add("§aCurrent Level: §e" + currentLevel);
            lore.add("§6Cost: §e" + (opt.getPricePerLevel() * currentLevel * baseConfig.getQuantity()) + " coins");
        } else {
            lore.add("§7Not selected");
        }

        lore.add("");
        lore.add("§eClick to cycle level");
        lore.add("§8" + opt.getEnchantment().getKey().getKey());

        Material displayMaterial = currentLevel > 0 ? Material.ENCHANTED_BOOK : Material.BOOK;
        String displayName = currentLevel > 0
            ? "§b§l" + opt.getEnchantment().getName() + " " + currentLevel
            : "§7" + opt.getEnchantment().getName();

        return createItem(displayMaterial, displayName, lore.toArray(new String[0]));
    }

    private ItemStack createSelectionDisplay() {
        List<String> lore = new ArrayList<>();
        lore.add("§7Item: §e" + baseConfig.getItem().getName());
        lore.add("§7Quantity: §e" + baseConfig.getQuantity());
        lore.add("");

        if (selectedEnchantments.isEmpty()) {
            lore.add("§7No enchantments selected");
        } else {
            lore.add("§b§lSelected Enchantments:");
            for (Map.Entry<Enchantment, Integer> entry : selectedEnchantments.entrySet()) {
                lore.add("§7• " + entry.getKey().getName() + " " + entry.getValue());
            }
        }

        return createItem(Material.WRITABLE_BOOK, "§e§lCurrent Selection", lore.toArray(new String[0]));
    }

    private int calculateEnchantmentCost() {
        int cost = 0;
        for (Map.Entry<Enchantment, Integer> entry : selectedEnchantments.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();

            int pricePerLevel = baseConfig.getItem().getAvailableEnchantments().stream()
                .filter(opt -> opt.getEnchantment().equals(ench))
                .findFirst()
                .map(ShopItem.EnchantmentOption::getPricePerLevel)
                .orElse(0);

            cost += pricePerLevel * level * baseConfig.getQuantity();
        }
        return cost;
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
