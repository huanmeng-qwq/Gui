package me.huanmeng.gui.slot.function;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * A simplified click interface that provides only the button and player.
 * <p>
 * This interface extends {@link ButtonClickInterface} and provides a default
 * implementation that extracts the button and player from the click data.
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
@FunctionalInterface
public interface ButtonSimpleClickInterface extends ButtonClickInterface {
    @Override
    default Result handleClick(@NonNull ClickData clickData) {
        if (clickData.button == null) {
            throw new IllegalArgumentException("button is null");
        }
        return onPlayerClick(clickData.button, clickData.player);
    }

    /**
     * Handles a click event with simplified parameters.
     *
     * @param button the button that was clicked
     * @param player the player who clicked
     * @return the result indicating how to handle the event
     */
    @NonNull
    Result onPlayerClick(@NonNull Button button, @NonNull Player player);
}
