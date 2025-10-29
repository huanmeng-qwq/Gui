package me.huanmeng.gui.gui.interfaces;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.util.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface for customizing GUI rendering and button lookup behavior.
 * <p>
 * This interface provides hooks for intercepting and modifying the GUI's internal operations,
 * specifically how items are placed in inventory slots and how button clicks are resolved.
 * Implementing this interface allows for advanced customizations such as:
 * <ul>
 *   <li>Custom item rendering logic (e.g., applying NBT tags, enchantment glows)</li>
 *   <li>Alternative button lookup strategies (e.g., based on custom metadata)</li>
 *   <li>Debugging and logging of GUI operations</li>
 * </ul>
 *
 * <p>
 * A default implementation {@link GuiHandlerDefaultImpl} is provided for standard behavior.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/11/19
 * @see GuiHandlerDefaultImpl
 * @see AbstractGui
 */
public interface GuiHandler {
    /**
     * Called when an item needs to be set in the inventory for a button.
     * <p>
     * This method is invoked during GUI rendering when the library needs to place a button's
     * ItemStack into the actual Bukkit inventory. Implementations can modify the item before
     * it's placed, add NBT data, or apply custom transformations.
     *
     * <p>
     * <b>Example Custom Implementation:</b>
     * <pre>{@code
     * @Override
     * public void onSetItem(AbstractGui<?> gui, Inventory inventory, GuiButton button, ItemStack itemStack) {
     *     if (itemStack != null) {
     *         // Add enchantment glow to all items
     *         itemStack = new ItemBuilder(itemStack).glow(true).build();
     *     }
     *     inventory.setItem(button.getIndex(), itemStack);
     * }
     * }</pre>
     *
     *
     * @param gui       the GUI being rendered, never null
     * @param inventory the Bukkit inventory to modify, never null
     * @param button    the button whose item is being placed, never null
     * @param itemStack the ItemStack to place, or null to clear the slot
     */
    void onSetItem(@NonNull AbstractGui<?> gui, @NonNull Inventory inventory, @NonNull GuiButton button, @Nullable ItemStack itemStack);

    /**
     * Queries which button was clicked based on the inventory click event.
     * <p>
     * This method allows for custom button lookup logic, which can be useful for:
     * <ul>
     *   <li>Mapping multiple slots to the same button</li>
     *   <li>Implementing custom button identification based on NBT or metadata</li>
     *   <li>Debugging button click detection</li>
     * </ul>
     * Returning {@code null} causes the library to use its default button lookup mechanism
     * (based on slot index).
     *
     *
     * @param e   the inventory click event, never null
     * @param gui the GUI in which the click occurred, never null
     * @return the button that was clicked, or {@code null} to use default lookup behavior
     */
    @Nullable
    default GuiButton queryClickButton(@NonNull InventoryClickEvent e, AbstractGui<?> gui) {
        return null;
    }

    /**
     * Default implementation of {@link GuiHandler} that provides standard behavior.
     * <p>
     * This implementation:
     * <ul>
     *   <li>Places items directly into the inventory at the button's slot index</li>
     *   <li>Clears slots when the ItemStack is null</li>
     *   <li>Uses {@link ItemBuilder} to ensure proper item copying</li>
     *   <li>Relies on default button lookup (returns null from {@link #queryClickButton})</li>
     * </ul>
     *
     */
    class GuiHandlerDefaultImpl implements GuiHandler {

        @Override
        public void onSetItem(@NonNull AbstractGui<?> gui, @NonNull Inventory inventory, @NonNull GuiButton button, @Nullable ItemStack itemStack) {
            int index = button.getIndex();
            if (itemStack == null) {
                inventory.setItem(index, null);
                return;
            }
            ItemBuilder itemBuilder = new ItemBuilder(itemStack);
            inventory.setItem(index, itemBuilder.build());
        }
    }
}
