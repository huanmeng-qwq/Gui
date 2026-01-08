package me.huanmeng.gui.enums;

import me.huanmeng.gui.AbstractGui;
import me.huanmeng.gui.GuiButton;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.interfaces.CustomResultHandler;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.event.Cancellable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents the result of a button click, determining how the inventory event should be handled.
 *
 * <p>This class defines standard click result behaviors. For custom result handling,
 * use {@link AbstractGui#customResultHandler(CustomResultHandler)}.
 *
 * <p><b>Standard Results:</b>
 * <ul>
 *   <li>{@link #CANCEL} - Cancels the event, preventing vanilla inventory behavior</li>
 *   <li>{@link #ALLOW} - Allows the event to proceed normally</li>
 *   <li>{@link #CANCEL_UPDATE} - Cancels and refreshes the clicked slot</li>
 *   <li>{@link #CANCEL_UPDATE_ALL} - Cancels and refreshes all GUI slots</li>
 *   <li>{@link #CANCEL_CLOSE} - Cancels and closes the GUI</li>
 *   <li>{@link #CLEAR} - Cancels and clears the item stack (temporary until refresh)</li>
 *   <li>{@link #DECREMENT} - Cancels and decreases item amount (temporary until refresh)</li>
 *   <li>{@link #INCREMENTAL} - Cancels and increases item amount (temporary until refresh)</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * Button button = Button.of(itemStack, clickData -> {
 *     // Handle the click
 *     if (clickData.player.hasPermission("admin")) {
 *         // Allow inventory modification
 *         return Result.ALLOW;
 *     } else {
 *         // Cancel and close GUI
 *         return Result.CANCEL_CLOSE;
 *     }
 * });
 * }</pre>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see CustomResultHandler
 */
public class Result {
    /**
     * Cancels the inventory event.
     *
     * <p>This prevents the default Bukkit inventory behavior from executing,
     * such as moving items or modifying the inventory.
     *
     * @see Cancellable#setCancelled(boolean)
     */
    public static final Result CANCEL = new Result(true);

    /**
     * Cancels the event and refreshes the current slot's item.
     *
     * <p>After the click is processed, the item in the clicked slot will be
     * updated to reflect its current state in the GUI.
     */
    public static final Result CANCEL_UPDATE = new Result(CANCEL);

    /**
     * Cancels the event and refreshes all items in the GUI.
     *
     * <p>After the click is processed, all button items in the GUI will be
     * updated to reflect their current state.
     */
    public static final Result CANCEL_UPDATE_ALL = new Result(CANCEL);

    /**
     * Cancels the event and closes the GUI.
     *
     * @see AbstractGui#close(boolean, boolean)
     */
    public static final Result CANCEL_CLOSE = new Result(CANCEL);

    /**
     * Allows the inventory event to proceed normally.
     *
     * <p>The vanilla Bukkit inventory behavior will execute, such as
     * picking up, moving, or dropping items.
     */
    public static final Result ALLOW = new Result(false);

    /**
     * Removes the entire ItemStack from the slot.
     *
     * <p>The item will be cleared temporarily. Calling {@link AbstractGui#refresh(boolean)}
     * will restore the item to its original state.
     */
    public static final Result CLEAR = new Result(CANCEL);

    /**
     * Decreases the item amount by one.
     *
     * <p>The item amount will be decremented temporarily. Calling {@link AbstractGui#refresh(boolean)}
     * will reset the amount to its original value.
     */
    public static final Result DECREMENT = new Result(CANCEL);

    /**
     * Increases the item amount by one.
     *
     * <p>The item amount will be incremented temporarily. Calling {@link AbstractGui#refresh(boolean)}
     * will reset the amount to its original value.
     */
    public static final Result INCREMENTAL = new Result(CANCEL);

    /**
     * Whether the inventory event should be cancelled.
     */
    private final boolean cancel;

    /**
     * Creates a new Result with the specified cancellation state.
     *
     * @param cancel whether to cancel the inventory event
     */
    protected Result(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Creates a new Result copying the cancellation state from another Result.
     *
     * @param result the result to copy from
     */
    protected Result(Result result) {
        this.cancel = result.cancel;
    }

    /**
     * Returns whether this result indicates the event should be cancelled.
     *
     * @return true if the event should be cancelled
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Creates a forward result that delegates the click to another slot.
     *
     * <p>When returned from a button click handler, the click will be forwarded
     * to the button at the specified slot, triggering its click handler instead.
     *
     * @param forwardSlot the slot index to forward the click to
     * @param cancel whether to cancel the original event
     * @return a Forward result
     */
    public static Result forward(int forwardSlot, boolean cancel) {
        return new Forward(cancel, Slot.of(forwardSlot));
    }

    /**
     * Creates a forward result that delegates the click to another slot.
     *
     * <p>The original event will be cancelled.
     *
     * @param forwardSlot the slot index to forward the click to
     * @return a Forward result with cancellation
     */
    public static Result forward(int forwardSlot) {
        return new Forward(true, Slot.of(forwardSlot));
    }

    /**
     * Creates a forward result that delegates the click to another slot.
     *
     * @param forwardSlot the Slot to forward the click to
     * @param cancel whether to cancel the original event
     * @return a Forward result
     */
    public static Result forward(Slot forwardSlot, boolean cancel) {
        return new Forward(cancel, forwardSlot);
    }

    /**
     * Creates a forward result that delegates the click to another slot.
     *
     * <p>The original event will be cancelled.
     *
     * @param forwardSlot the Slot to forward the click to
     * @return a Forward result with cancellation
     */
    public static Result forward(Slot forwardSlot) {
        return new Forward(true, forwardSlot);
    }

    /**
     * Special result that forwards a click to another button.
     *
     * <p>This allows creating "alias" buttons that delegate their behavior
     * to other buttons in the GUI.
     */
    public static class Forward extends Result {
        /**
         * The slot to forward the click to.
         */
        private final Slot forward;

        /**
         * Creates a new Forward result.
         *
         * @param cancel whether to cancel the original event
         * @param forward the slot to forward the click to
         */
        protected Forward(boolean cancel, Slot forward) {
            super(cancel);
            this.forward = forward;
        }

        /**
         * Forwards the click to the target button.
         *
         * <p>Retrieves the button at the forward slot and invokes its click handler
         * with modified click data pointing to the forward slot.
         *
         * @param clickData the original click data
         * @return the result from the forwarded button's click handler, or {@link #ALLOW} if no button exists
         */
        public Result forwardClick(@NonNull ClickData clickData) {
            GuiButton forwardButton = clickData.gui.getButton(forward);
            if (forwardButton == null) {
                return Result.ALLOW;
            }
            return forwardButton.onClick(new ClickData(clickData.player, clickData.gui, forward, clickData.slot, forwardButton.getButton(), clickData.event, clickData.click, clickData.action, clickData.slotType, clickData.slotKey, clickData.hotBarKey));
        }
    }
}