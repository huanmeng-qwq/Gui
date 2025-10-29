package me.huanmeng.gui.gui.button;

import me.huanmeng.gui.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * A simple button implementation that uses a static ItemStack for display.
 * <p>
 * Unlike {@link BaseButton} which uses {@link me.huanmeng.gui.gui.button.function.PlayerItemInterface}
 * for dynamic item display, this implementation stores a single ItemStack that is shown to all players.
 *
 * <p>
 * This class is less flexible than BaseButton and is scheduled for removal. Use {@link BaseButton}
 * or the factory methods in {@link Button} instead.
 *
 *
 * @author huanmeng_qwq
 * @since 2024/12/27
 * @deprecated Scheduled for removal in version 2.6.0. Use {@link BaseButton} or {@link Button#of(ItemStack, PlayerClickInterface)} instead.
 */
@ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
@Deprecated
public class SimpleItemButton implements Button {
    /**
     * The ItemStack to display for this button.
     */
    private final ItemStack itemStack;

    /**
     * The click handler for this button, or null if no click handling is needed.
     */
    private final PlayerClickInterface clickable;

    /**
     * Constructs a new SimpleItemButton with the specified item and click handler.
     *
     * @param itemStack the ItemStack to display
     * @param clickable the click handler, or null
     */
    public SimpleItemButton(ItemStack itemStack, PlayerClickInterface clickable) {
        this.itemStack = itemStack;
        this.clickable = clickable;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns the same ItemStack regardless of the viewing player.
     * </p>
     */
    @Override
    public @Nullable ItemStack getShowItem(@NonNull Player player) {
        return this.itemStack;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the click handler if present, otherwise returns {@link Result#CANCEL}.
     * </p>
     */
    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        if (clickable == null) {
            return Result.CANCEL;
        }
        return this.clickable.onClick(clickData);
    }

}
