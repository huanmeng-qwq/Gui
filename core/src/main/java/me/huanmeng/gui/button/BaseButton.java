package me.huanmeng.gui.button;

import me.huanmeng.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.button.function.ItemProvider;
import me.huanmeng.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The base implementation of the {@link Button} interface.
 * <p>
 * This class provides a simple button implementation that delegates item display to a
 * {@link ItemProvider} and click handling to a {@link PlayerClickInterface}.
 * It serves as the foundation for most button types in the GUI system.
 *
 * <p>
 * Both the item provider and click interface are optional (nullable). If the item provider
 * is null, the button displays no item. If the click interface is null, clicks are cancelled
 * with no additional behavior.
 *
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
public class BaseButton implements Button {
    /**
     * The provider supplying the ItemStack to display, or null if no item should be displayed.
     */
    @Nullable
    private final ItemProvider itemProvider;

    /**
     * The interface handling click events, or null if no special click handling is needed.
     */
    @Nullable
    private final PlayerClickInterface playerClickInterface;

    /**
     * Constructs a new BaseButton with the specified item provider and click interface.
     *
     * @param itemProvider the provider supplying the item to display, or null
     * @param playerClickInterface the interface handling clicks, or null
     */
    public BaseButton(@Nullable ItemProvider itemProvider, @Nullable PlayerClickInterface playerClickInterface) {
        this.itemProvider = itemProvider;
        this.playerClickInterface = playerClickInterface;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the ItemStack from the item provider, or null if no provider is set.
     * </p>
     */
    @Override
    @Nullable
    public ItemStack getShowItem(@NonNull Player player) {
        if (itemProvider == null) {
            return null;
        }
        return itemProvider.get(player);
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
