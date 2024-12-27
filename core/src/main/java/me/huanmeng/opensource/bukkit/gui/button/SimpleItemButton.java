package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickInterface;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2024/12/27<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class SimpleItemButton implements Button {
    private final ItemStack itemStack;
    private final PlayerClickInterface clickable;

    public SimpleItemButton(ItemStack itemStack, PlayerClickInterface clickable) {
        this.itemStack = itemStack;
        this.clickable = clickable;
    }

    @Override
    public @Nullable ItemStack getShowItem(@NonNull Player player) {
        return this.itemStack;
    }

    @Override
    public @NonNull Result onClick(@NonNull AbstractGui<?> gui, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey, @NonNull InventoryClickEvent e) {
        return this.clickable.onClick(gui, slot, player, click, action, slotType, slotKey, hotBarKey);
    }
}
