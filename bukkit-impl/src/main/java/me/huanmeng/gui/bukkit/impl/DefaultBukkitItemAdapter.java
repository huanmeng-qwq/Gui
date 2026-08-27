package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.bukkit.api.BukkitItemAdapter;
import org.bukkit.inventory.ItemStack;

final class DefaultBukkitItemAdapter implements BukkitItemAdapter {
    @Override public ItemStack copyForInventory(ItemStack item) {
        return item == null ? null : item.clone();
    }
}
