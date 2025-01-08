package me.huanmeng.opensource.bukkit.gui.button.function.page;

import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.impl.AbstractGuiPage;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageArea;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButtonType;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickPageButtonInterface {

    /**
     * 点击事件
     *
     * @return {@link Result}
     */
    @NonNull
    default Result onClick(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull PageButtonType buttonType, @NonNull ClickData clickData) {
        return onClick(gui, pageArea, buttonType, clickData.slot, clickData.player, clickData.click, clickData.action, clickData.slotType, clickData.slotKey, clickData.hotBarKey);
    }

    /**
     * 点击事件
     *
     * @param gui        gui
     * @param pageArea   area
     * @param buttonType 按钮类型
     * @param slot       位置
     * @param player     玩家
     * @param click      点击类型
     * @param action     点击事件
     * @param slotType   位置
     * @param slotKey    pos
     * @param hotBarKey  热键
     * @return {@link Result}
     */
    @NonNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    Result onClick(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull PageButtonType buttonType, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action,
                   InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey);

    PlayerClickPageButtonInterface SIMPLE = new PlayerClickPageButtonInterface() {
        @Override
        public @NonNull Result onClick(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull PageButtonType buttonType, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey) {
            buttonType.changePage(pageArea);
            return Result.CANCEL_UPDATE_ALL;
        }
    };

    static PlayerClickPageButtonInterface simple() {
        return SIMPLE;
    }
}
