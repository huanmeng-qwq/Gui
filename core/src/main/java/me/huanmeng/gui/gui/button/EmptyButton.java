package me.huanmeng.gui.gui.button;

import me.huanmeng.gui.gui.button.function.PlayerItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * A display-only button implementation with no click handling.
 * <p>
 * This button displays an item but does not provide any custom click behavior beyond
 * canceling the default inventory event. It is equivalent to using {@link Button#of(PlayerItemInterface)}.
 *
 * <p>
 * This class is scheduled for removal. Use {@link Button#of(PlayerItemInterface)} instead.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @deprecated Scheduled for removal in version 2.6.0. Use {@link Button#of(PlayerItemInterface)} instead.
 */
@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
public class EmptyButton implements Button {
    /**
     * The interface providing the ItemStack to display.
     */
    @NonNull
    private final PlayerItemInterface userItemInterface;

    /**
     * Constructs a new EmptyButton with the specified item provider.
     *
     * @param userItemInterface the interface providing the item to display
     */
    public EmptyButton(@NonNull PlayerItemInterface userItemInterface) {
        this.userItemInterface = userItemInterface;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the ItemStack from the item interface.
     * </p>
     */
    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        return userItemInterface.get(player);
    }
}
