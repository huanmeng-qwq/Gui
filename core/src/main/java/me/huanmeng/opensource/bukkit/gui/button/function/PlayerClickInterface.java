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
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickInterface {
    /**
     * 点击事件
     *
     * @param gui
     * @param slot      位置
     * @param player    玩家
     * @param click     点击类型
     * @param action    点击事件
     * @param slotType  位置
     * @param slotKey   pos
     * @param hotBarKey 热键
     * @return {@link Result}
     */
    @NonNull
    Result onClick(@NonNull AbstractGui<?> gui, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action,
                   InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey);
}
