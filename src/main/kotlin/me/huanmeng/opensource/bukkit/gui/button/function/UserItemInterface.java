package me.huanmeng.opensource.bukkit.gui.button.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface UserItemInterface {
    /**
     * 获取物品
     *
     * @param player 玩家
     * @return 物品
     */
    ItemStack get(Player player);
}
