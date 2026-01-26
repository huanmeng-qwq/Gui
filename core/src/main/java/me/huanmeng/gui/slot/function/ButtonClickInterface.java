package me.huanmeng.gui.slot.function;

import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import org.jspecify.annotations.NonNull;

/**
 * Functional interface for handling button click events on slots.
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
@FunctionalInterface
public interface ButtonClickInterface {
    /**
     * Handles a click event on a slot.
     *
     * @param clickData the click event data
     * @return the result indicating how to handle the event
     */
    Result handleClick(@NonNull ClickData clickData);
}
