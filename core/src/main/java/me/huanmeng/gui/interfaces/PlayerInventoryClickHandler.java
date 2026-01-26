package me.huanmeng.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NonNull;

/**
 * Interface for handling clicks in the player's inventory when a GUI is open.
 * <p>
 * When a GUI inventory is displayed, the player sees both the custom GUI (top) and their own
 * inventory (bottom). This interface allows GUIs to intercept and handle clicks that occur
 * in the player's inventory portion of the view.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * public class MyGui extends AbstractGui<MyGui> implements PlayerInventoryClickHandler {
 *     @Override
 *     public boolean onPlayerInventoryClick(Player player, Inventory playerInventory, ClickType clickType,
 *                                           InventoryAction action, InventoryType.SlotType slotType,
 *                                           int hotBar, InventoryClickEvent e) {
 *         // Allow players to freely interact with their inventory
 *         if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
 *             return false; // Don't cancel the event
 *         }
 *         // Block shift-clicking to prevent moving items to GUI
 *         return true; // Cancel the event
 *     }
 * }
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 1.0
 * @see InventoryClickEvent
 */
public interface PlayerInventoryClickHandler {
    /**
     * Called when a player clicks in their own inventory while viewing this GUI.
     * <p>
     * This method is invoked whenever a click event occurs in the player's inventory
     * (the bottom inventory) of the GUI view. Return {@code true} to cancel the event
     * and prevent the default behavior, or {@code false} to allow it.
     * </p>
     *
     * @param player          the player who performed the click, never null
     * @param playerInventory the player's inventory, never null
     * @param clickType       the type of click performed (LEFT, RIGHT, SHIFT_LEFT, etc.), never null
     * @param action          the inventory action performed (PICKUP_ALL, MOVE_TO_OTHER_INVENTORY, etc.), never null
     * @param slotType        the type of slot that was clicked (QUICKBAR, ARMOR, etc.), never null
     * @param hotBar          the hotbar button pressed (0-8 for number keys, -1 if none)
     * @param e               the underlying Bukkit inventory click event, never null
     * @return {@code true} to cancel the event, {@code false} to allow normal processing
     */
    boolean onPlayerInventoryClick(@NonNull Player player, @NonNull Inventory playerInventory, @NonNull ClickType clickType,
                                   @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType,
                                   int hotBar, @NonNull InventoryClickEvent e);
}
