package me.huanmeng.gui.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Interface for handling clicks on empty (non-button) slots in a GUI.
 * <p>
 * This interface allows GUIs to respond to clicks on slots that don't have a button assigned.
 * This is useful for implementing custom behavior when players click on empty areas of the GUI,
 * such as preventing item placement or displaying messages.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * public class MyGui extends AbstractGui<MyGui> implements GuiEmptyItemClick {
 *     @Override
 *     public boolean onClickEmptyButton(Player player, int slot, ClickType clickType,
 *                                       InventoryAction action, InventoryType.SlotType slotType,
 *                                       int hotBar, InventoryClickEvent e) {
 *         player.sendMessage("You clicked an empty slot at position: " + slot);
 *         return true; // Cancel the event to prevent item placement
 *     }
 * }
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see me.huanmeng.gui.gui.AbstractGui#cancelClickOther
 */
public interface GuiEmptyItemClick {
    /**
     * Called when a player clicks on an empty slot (a slot without a button) in the GUI.
     * <p>
     * This method is invoked only when a clicked slot has no associated {@link me.huanmeng.gui.gui.button.Button}.
     * Return {@code true} to cancel the event and prevent default behavior (like placing items),
     * or {@code false} to allow the event to proceed.
     * </p>
     *
     * @param player    the player who performed the click, never null
     * @param slot      the raw slot index that was clicked (0-based)
     * @param clickType the type of click performed (LEFT, RIGHT, SHIFT_LEFT, etc.), never null
     * @param action    the inventory action performed (PICKUP_ALL, PLACE_ONE, etc.), never null
     * @param slotType  the type of slot that was clicked (CONTAINER, QUICKBAR, etc.), never null
     * @param hotBar    the hotbar button pressed (0-8 for number keys, -1 if none)
     * @param e         the underlying Bukkit inventory click event, never null
     * @return {@code true} to cancel the event, {@code false} to allow normal processing
     */
    boolean onClickEmptyButton(@NonNull Player player, int slot, @NonNull ClickType clickType, @NonNull InventoryAction action,
                               InventoryType.@NonNull SlotType slotType, int hotBar, @NonNull InventoryClickEvent e);
}
