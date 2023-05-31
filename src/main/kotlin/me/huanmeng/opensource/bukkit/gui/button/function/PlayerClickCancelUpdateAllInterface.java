package me.huanmeng.opensource.bukkit.gui.button.function;

import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickCancelUpdateAllInterface extends PlayerClickInterface {
    @Override
    default Result onClick(Slot slot, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey) {
        onPlayerClick(player, click, action, slotType, slotKey, hotBarKey);
        return Result.CANCEL_UPDATE_ALL;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL_UPDATE_ALL}
     *
     * @param player    玩家
     * @param click     点击类型
     * @param action    点击事件
     * @param slotType  位置
     * @param slot      位置
     * @param hotBarKey 热键
     */
    void onPlayerClick(Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slot, int hotBarKey);
}
