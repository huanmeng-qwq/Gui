package me.huanmeng.opensource.bukkit.gui.button.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 2022/6/16<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface UserItemInterface {
    ItemStack get(Player player);
}
