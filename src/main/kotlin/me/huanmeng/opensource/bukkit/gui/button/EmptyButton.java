package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.button.function.UserItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/3/17<br>
 * Gui<br>
 * 仅显示型按钮
 *
 * @author huanmeng_qwq
 */
public class EmptyButton implements Button {
    @NonNull
    private final UserItemInterface userItemInterface;

    public EmptyButton(@NonNull UserItemInterface userItemInterface) {
        this.userItemInterface = userItemInterface;
    }

    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        return userItemInterface.get(player);
    }
}
