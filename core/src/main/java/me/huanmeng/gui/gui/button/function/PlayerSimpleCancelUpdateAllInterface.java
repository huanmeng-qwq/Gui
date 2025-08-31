package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;


@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
@FunctionalInterface
public interface PlayerSimpleCancelUpdateAllInterface extends PlayerClickInterface {
    @Override
    default Result onClick(@NonNull ClickData clickData) {
        onPlayerClick(clickData.player);
        return Result.CANCEL_UPDATE_ALL;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL_UPDATE_ALL}
     *
     * @param player 玩家
     */
    void onPlayerClick(@NonNull Player player);
}
