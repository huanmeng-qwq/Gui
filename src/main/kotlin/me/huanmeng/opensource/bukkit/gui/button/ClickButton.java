package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickInterface;
import me.huanmeng.opensource.bukkit.gui.button.function.UserItemInterface;
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
 * 2023/3/17<br>
 * Gui<br>
 * 点击型按钮
 *
 * @author huanmeng_qwq
 */
public class ClickButton implements Button {
    @NonNull
    private final UserItemInterface userItemInterface;
    @NonNull
    private final PlayerClickInterface playerClickInterface;

    public ClickButton(@NonNull UserItemInterface userItemInterface, @NonNull PlayerClickInterface playerClickInterface) {
        this.userItemInterface = userItemInterface;
        this.playerClickInterface = playerClickInterface;
    }

    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        return userItemInterface.get(player);
    }

    @NonNull
    @Override
    public Result onClick(@NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey, @NonNull InventoryClickEvent e) {
        return playerClickInterface.onClick(slot, player, click, action, slotType, slotKey, hotBarKey);
    }
}
