package me.huanmeng.gui.button;

import me.huanmeng.gui.button.function.ClickHandler;
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
 * {@link ItemProvider} and click handling to a {@link ClickHandler}.
 * It serves as the foundation for most button types in the GUI system.
 *
 * <p>
 * Both the item provider and click handler are optional (nullable). If the item provider
 * is null, the button displays no item. If the click handler is null, clicks are cancelled
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
     * The handler for click events, or null if no special click handling is needed.
     */
    @Nullable
    private final ClickHandler clickHandler;

    /**
     * Constructs a new BaseButton with the specified item provider and click handler.
     *
     * @param itemProvider the provider supplying the item to display, or null
     * @param clickHandler the handler for clicks, or null
     */
    public BaseButton(@Nullable ItemProvider itemProvider, @Nullable ClickHandler clickHandler) {
        this.itemProvider = itemProvider;
        this.clickHandler = clickHandler;
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
     * Delegates to the click handler if present, otherwise returns {@link Result#CANCEL}.
     * </p>
     */
    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        if (clickHandler == null) {
            return Result.CANCEL;
        }
        return clickHandler.onClick(clickData);
    }
}
