package me.huanmeng.gui.slot.function;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface ButtonSimpleClickInterface extends ButtonClickInterface {
    @Override
    default Result onClick(@NonNull ClickData clickData) {
        if (clickData.button == null) {
            throw new IllegalArgumentException("button is null");
        }
        return onPlayerClick(clickData.button, clickData.player);
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
