package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.ItemView;
import org.bukkit.inventory.ItemStack;

public final class BukkitItemView implements ItemView {
    private final ItemStack item;

    public BukkitItemView(ItemStack item) {
        this.item = item == null ? null : item.clone();
    }

    public ItemStack item() {
        return item == null ? null : item.clone();
    }

    /** @deprecated Use {@link #item()}. */
    @Deprecated
    public ItemStack getItem() {
        return item();
    }
}
