package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

/**
 * 2024/10/23<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
@FunctionalInterface
public interface PlayerSimpleClickCancelInterface extends PlayerClickInterface {
    @Override
    default Result onClick(@NonNull ClickData clickData) {
        onPlayerClick(clickData.player, clickData.click);
        return Result.CANCEL;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL}
     *
     * @param player 玩家
     */
    void onPlayerClick(@NonNull Player player, @NonNull ClickType click);
}
