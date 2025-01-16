package me.huanmeng.opensource.bukkit.gui.button.function;

import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/10/23<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerSimpleClickUpdateAllInterface extends PlayerClickInterface {
    @Override
    default Result onClick(@NonNull ClickData clickData) {
        onPlayerClick(clickData.player, clickData.click);
        return Result.CANCEL_UPDATE_ALL;
    }

    /**
     * 点击事件, 将会自动返回{@link Result#CANCEL_UPDATE_ALL}
     *
     * @param player 玩家
     */
    void onPlayerClick(@NonNull Player player, @NonNull ClickType click);
}
