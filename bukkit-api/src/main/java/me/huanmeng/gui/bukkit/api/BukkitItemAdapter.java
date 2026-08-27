package me.huanmeng.gui.bukkit.api;

import org.bukkit.inventory.ItemStack;

public interface BukkitItemAdapter {
    ItemStack copyForInventory(ItemStack item);
}
