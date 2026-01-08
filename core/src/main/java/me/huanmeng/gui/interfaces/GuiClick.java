package me.huanmeng.gui.interfaces;

import me.huanmeng.gui.GuiButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Interface for handling click preprocessing in GUI inventories.
 * <p>
 * This interface allows GUIs to intercept and validate click events before they are processed
 * by button handlers. Implementing this interface enables custom click validation logic,
 * permission checks, or conditional click handling.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * public class MyGui extends AbstractGui<MyGui> implements GuiClick {
 *     @Override
 *     public boolean allowClick(Player player, GuiButton button, ClickType clickType,
 *                               InventoryAction action, InventoryType.SlotType slotType,
 *                               int slot, int hotBar, InventoryClickEvent e) {
 *         // Only allow left clicks
 *         if (clickType != ClickType.LEFT) {
 *             player.sendMessage("Only left click is allowed!");
 *             return false;
 *         }
 *         return true;
 *     }
 * }
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see GuiButton
 * @see InventoryClickEvent
 */
public interface GuiClick {
    /**
     * Called before a button click is processed to determine if the click should be allowed.
     * <p>
     * This method is invoked during the click event preprocessing phase, before the button's
     * {@link me.huanmeng.gui.button.Button#onClick} method is called. Returning {@code false}
     * will cancel the click event and prevent further processing.
     * </p>
     *
     * @param player    the player who performed the click, never null
     * @param button    the GUI button that was clicked, never null
     * @param clickType the type of click performed (LEFT, RIGHT, SHIFT_LEFT, etc.), never null
     * @param action    the inventory action performed (PICKUP_ALL, PLACE_ONE, etc.), never null
     * @param slotType  the type of slot that was clicked (CONTAINER, QUICKBAR, etc.), never null
     * @param slot      the raw slot index that was clicked (0-based)
     * @param hotBar    the hotbar button pressed (0-8 for number keys, -1 if none)
     * @param e         the underlying Bukkit inventory click event, never null
     * @return {@code true} to allow the click to be processed, {@code false} to cancel it
     */
    boolean allowClick(@NonNull Player player, @NonNull GuiButton button, @NonNull ClickType clickType,
                       @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot,
                       int hotBar, @NonNull InventoryClickEvent e);
}
