package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
@FunctionalInterface
public interface PlayerClickCancelInterface extends PlayerClickInterface {
    @Override
    default Result onClick(@NonNull ClickData clickData) {
        onPlayerClick(clickData.gui, clickData.player, clickData.click, clickData.action, clickData.slotType, clickData.slotKey, clickData.hotBarKey);
        return Result.CANCEL;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL}
     *
     * @param gui       {@link AbstractGui}
     * @param player    玩家
     * @param click     点击类型
     * @param action    点击事件
     * @param slot      位置
     * @param hotBarKey 热键
     */
    void onPlayerClick(@NonNull AbstractGui<?> gui, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBarKey);
}
