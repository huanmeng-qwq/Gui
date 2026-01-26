package me.huanmeng.gui.slot.function;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Functional interface for determining whether a button can be placed on a slot.
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
@FunctionalInterface
public interface ButtonPlaceInterface {
    /**
     * Determines whether a button can be placed on the specified slot.
     *
     * @param slot   the slot where the button would be placed
     * @param button the button to place
     * @param player the player for whom the button is being placed
     * @return true if the button can be placed, false otherwise
     */
    boolean canPlace(@NonNull Slot slot, @NonNull Button button, @NonNull Player player);

    ButtonPlaceInterface ALWAYS_TRUE = (slot, button, player) -> true;
    ButtonPlaceInterface ALWAYS_FALSE = (slot, button, player) -> false;

    static ButtonPlaceInterface alwaysTrue() {
        return ALWAYS_TRUE;
    }

    static ButtonPlaceInterface alwaysFalse() {
        return ALWAYS_FALSE;
    }
}
