package me.huanmeng.gui.button;

import me.huanmeng.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.button.function.PlayerItemInterface;
import me.huanmeng.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The base implementation of the {@link Button} interface.
 * <p>
 * This class provides a simple button implementation that delegates item display to a
 * {@link PlayerItemInterface} and click handling to a {@link PlayerClickInterface}.
 * It serves as the foundation for most button types in the GUI system.
 *
 * <p>
 * Both the item interface and click interface are optional (nullable). If the item interface
 * is null, the button displays no item. If the click interface is null, clicks are cancelled
 * with no additional behavior.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
public class BaseButton implements Button {
    /**
     * The interface providing the ItemStack to display, or null if no item should be displayed.
     */
    @Nullable
    private final PlayerItemInterface userItemInterface;

    /**
     * The interface handling click events, or null if no special click handling is needed.
     */
    @Nullable
    private final PlayerClickInterface playerClickInterface;

    /**
     * Constructs a new BaseButton with the specified item and click interfaces.
     *
     * @param userItemInterface the interface providing the item to display, or null
     * @param playerClickInterface the interface handling clicks, or null
     */
    public BaseButton(@Nullable PlayerItemInterface userItemInterface, @Nullable PlayerClickInterface playerClickInterface) {
        this.userItemInterface = userItemInterface;
        this.playerClickInterface = playerClickInterface;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the ItemStack from the item interface, or null if no interface is set.
     * </p>
     */
    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        if (userItemInterface == null) {
            return null;
        }
        return userItemInterface.get(player);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the click interface if present, otherwise returns {@link Result#CANCEL}.
     * </p>
     */
    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        if (playerClickInterface == null) {
            return Result.CANCEL;
        }
        return playerClickInterface.onClick(clickData);
    }
}
