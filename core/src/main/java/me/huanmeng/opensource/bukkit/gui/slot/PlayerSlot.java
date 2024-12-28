package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 代表玩家背包中的槽位
 *
 * @author huanmeng_qwq
 */
public class PlayerSlot implements Slot {
    @NonNull
    private final Slot slot;

    public PlayerSlot(@NonNull Slot slot) {
        this.slot = slot;
    }

    @Override
    public int getIndex() {
        return slot.getIndex();
    }

    @Override
    public @NonNull Result onClick(@NonNull AbstractGui<?> gui, @NonNull Button button, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBarKey, @NonNull InventoryClickEvent e) {
        return this.slot.onClick(gui, button, player, click, action, slotType, slot, hotBarKey, e);
    }

    @Override
    public boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return slot.tryPlace(button, player);
    }

    @Override
    public PlayerSlot asPlayer() {
        return this;
    }

    public Slot getSlot() {
        return slot;
    }
}
