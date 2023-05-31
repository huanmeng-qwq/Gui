package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;

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
    boolean tryPlace(Slot slot, Button button, Player player);
}
