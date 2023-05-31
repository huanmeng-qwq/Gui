package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.button.function.UserItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 2023/3/17<br>
 * Gui<br>
 * 仅显示型按钮
 *
 * @author huanmeng_qwq
 */
public class EmptyButton implements Button {
    private final UserItemInterface userItemInterface;

    public EmptyButton(UserItemInterface userItemInterface) {
        this.userItemInterface = userItemInterface;
    }

    @Override
    public ItemStack getShowItem(Player player) {
        return userItemInterface.get(player);
    }
}
