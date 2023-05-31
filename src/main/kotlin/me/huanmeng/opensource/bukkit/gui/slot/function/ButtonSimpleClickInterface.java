package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface ButtonSimpleClickInterface extends ButtonClickInterface {
    @NotNull
    @Override
    default Result onClick(@NotNull Slot slot, @NotNull Button button, @NotNull Player player, @NotNull ClickType click, @NotNull InventoryAction action, @NotNull InventoryType.SlotType slotType, int slotKey, int hotBarKey, @NotNull InventoryClickEvent e) {
        return onPlayerClick(button, player);
    }

    /**
     * 点击事件
     *
     * @param button 按钮
     * @param player 玩家
     * @return {@link Result}
     */
    @NonNull
    Result onPlayerClick(@NonNull Button button, @NonNull Player player);
}
