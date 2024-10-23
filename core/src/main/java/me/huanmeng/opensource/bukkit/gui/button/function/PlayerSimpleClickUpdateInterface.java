package me.huanmeng.opensource.bukkit.gui.button.function;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/10/23<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerSimpleClickUpdateInterface extends PlayerClickInterface {
    @Override
    @NonNull
    default Result onClick(@NonNull AbstractGui<?> gui, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey) {
        onPlayerClick(player, click);
        return Result.CANCEL_UPDATE;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL_UPDATE}
     *
     * @param player 玩家
     */
    void onPlayerClick(@NonNull Player player, @NonNull ClickType click);
}
