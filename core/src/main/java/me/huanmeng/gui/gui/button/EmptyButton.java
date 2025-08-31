package me.huanmeng.gui.gui.button;

import me.huanmeng.gui.gui.button.function.PlayerItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
public class EmptyButton implements Button {
    @NonNull
    private final PlayerItemInterface userItemInterface;

    public EmptyButton(@NonNull PlayerItemInterface userItemInterface) {
        this.userItemInterface = userItemInterface;
    }

    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        return userItemInterface.get(player);
    }
}
