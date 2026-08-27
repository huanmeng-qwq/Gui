package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.bukkit.api.BukkitInventoryAdapter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

final class DefaultBukkitInventoryAdapter implements BukkitInventoryAdapter {
    private final BukkitThreadGuard threadGuard;

    DefaultBukkitInventoryAdapter(BukkitThreadGuard threadGuard) {
        this.threadGuard = threadGuard;
    }

    @Override
    public Inventory createChest(InventoryHolder holder, int rows, String title) {
        threadGuard.checkRenderThread();
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be between 1 and 6");
        return Bukkit.createInventory(holder, rows * 9, title == null ? "" : title);
    }

    @Override
    public void set(Inventory inventory, int slot, ItemStack item) {
        threadGuard.checkRenderThread();
        inventory.setItem(slot, item);
    }

    @Override
    public void clear(Inventory inventory, int slot) {
        threadGuard.checkRenderThread();
        inventory.clear(slot);
    }
}
