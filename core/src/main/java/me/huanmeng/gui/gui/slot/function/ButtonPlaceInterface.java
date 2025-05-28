package me.huanmeng.gui.gui.slot.function;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface ButtonPlaceInterface {
    /**
     * 尝试放置
     *
     * @param slot   位置
     * @param button 按钮
     * @param player 玩家
     * @return 是否成功
     */
    boolean tryPlace(@NonNull Slot slot, @NonNull Button button, @NonNull Player player);

    ButtonPlaceInterface ALWAYS_TRUE = (slot, button, player) -> true;
    ButtonPlaceInterface ALWAYS_FALSE = (slot, button, player) -> false;

    static ButtonPlaceInterface alwaysTrue() {
        return ALWAYS_TRUE;
    }

    static ButtonPlaceInterface alwaysFalse() {
        return ALWAYS_FALSE;
    }
}
