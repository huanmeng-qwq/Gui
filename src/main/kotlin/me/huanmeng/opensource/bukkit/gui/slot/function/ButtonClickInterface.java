package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

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
    Result onClick(Slot slot, Button button, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e);
}
