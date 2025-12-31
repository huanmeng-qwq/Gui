package me.huanmeng.gui.example.shop;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

/**
 * Represents an item in the shop with complex configuration options.
 */
public class ShopItem {
    private final String id;
    private final String name;
    private final Material material;
    private final String description;
    private final int basePrice;
    private final ShopCategory category;
    private final int stock;
    private final List<EnchantmentOption> availableEnchantments;
    private final boolean customizable;

    public ShopItem(String id, String name, Material material, String description,
                    int basePrice, ShopCategory category, int stock, boolean customizable) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.stock = stock;
        this.customizable = customizable;
        this.availableEnchantments = new ArrayList<>();
    }

    public ShopItem addEnchantment(Enchantment enchantment, int maxLevel, int pricePerLevel) {
        availableEnchantments.add(new EnchantmentOption(enchantment, maxLevel, pricePerLevel));
        return this;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public String getDescription() { return description; }
    public int getBasePrice() { return basePrice; }
    public ShopCategory getCategory() { return category; }
    public int getStock() { return stock; }
    public boolean isCustomizable() { return customizable; }
    public List<EnchantmentOption> getAvailableEnchantments() {
        return Collections.unmodifiableList(availableEnchantments);
    }

    /**
     * Represents an enchantment option available for purchase.
     */
    public static class EnchantmentOption {
        private final Enchantment enchantment;
        private final int maxLevel;
        private final int pricePerLevel;

        public EnchantmentOption(Enchantment enchantment, int maxLevel, int pricePerLevel) {
            this.enchantment = enchantment;
            this.maxLevel = maxLevel;
            this.pricePerLevel = pricePerLevel;
        }

        public Enchantment getEnchantment() { return enchantment; }
        public int getMaxLevel() { return maxLevel; }
        public int getPricePerLevel() { return pricePerLevel; }
    }

    /**
     * Represents a purchase configuration with selected options.
     */
    public static class PurchaseConfig {
        private final ShopItem item;
        private final int quantity;
        private final Map<Enchantment, Integer> selectedEnchantments;
        private final String customName;

        public PurchaseConfig(ShopItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
            this.selectedEnchantments = new HashMap<>();
            this.customName = null;
        }

        public PurchaseConfig(ShopItem item, int quantity, Map<Enchantment, Integer> enchantments, String customName) {
            this.item = item;
            this.quantity = quantity;
            this.selectedEnchantments = new HashMap<>(enchantments);
            this.customName = customName;
        }

        public ShopItem getItem() { return item; }
        public int getQuantity() { return quantity; }
        public Map<Enchantment, Integer> getSelectedEnchantments() {
            return Collections.unmodifiableMap(selectedEnchantments);
        }
        public String getCustomName() { return customName; }

        public int getTotalPrice() {
            int total = item.getBasePrice() * quantity;
            for (Map.Entry<Enchantment, Integer> entry : selectedEnchantments.entrySet()) {
                Enchantment ench = entry.getKey();
                int level = entry.getValue();
                int pricePerLevel = item.getAvailableEnchantments().stream()
                    .filter(opt -> opt.getEnchantment().equals(ench))
                    .findFirst()
                    .map(EnchantmentOption::getPricePerLevel)
                    .orElse(0);
                total += pricePerLevel * level * quantity;
            }
            return total;
        }
    }
}
