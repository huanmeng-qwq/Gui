package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface ButtonClickInterface {
    /**
     * 点击事件
     *
     * @param slot      位置
     * @param button    按钮
     * @param player    玩家
     * @param click     点击类型
     * @param action    点击事件
     * @param slotType  位置类型
     * @param slotKey   位置
     * @param hotBarKey 热键
     * @param e         事件
     * @return {@link Result}
     */

    @NonNull
    Result onClick(@NonNull AbstractGui<?> gui, @NonNull Slot slot, @NonNull Button button, @NonNull Player player,
                   @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType,
                   int slotKey, int hotBarKey, @NonNull InventoryClickEvent e);
}
