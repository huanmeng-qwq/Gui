package me.huanmeng.gui.bukkit.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface BukkitInventoryAdapter {
    Inventory createChest(InventoryHolder holder, int rows, String title);

    void set(Inventory inventory, int slot, ItemStack item);

    void clear(Inventory inventory, int slot);
}
