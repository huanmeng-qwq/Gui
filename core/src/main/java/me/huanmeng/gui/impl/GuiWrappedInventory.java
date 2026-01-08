package me.huanmeng.gui.impl;

import me.huanmeng.gui.slot.Slots;
import me.huanmeng.gui.scheduler.Schedulers;
import me.huanmeng.gui.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A GUI wrapper around existing Bukkit inventory instances.
 * <p>
 * GuiWrappedInventory allows you to wrap an existing Bukkit {@link Inventory} and add
 * button functionality to it while preserving the original inventory's type, size, and
 * initial contents. This is useful when you want to add interactive buttons to vanilla
 * inventories like chests, hoppers, or other container types.
 *
 * <p>
 * Unlike {@link GuiCustom} which creates a new inventory from scratch, this wrapper
 * maintains the properties of the provided inventory. By default, it allows more
 * player interaction by disabling most click restrictions (cancelClickOther,
 * cancelClickBottom, etc. are set to false).
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * Inventory existingInventory = ...; // Some existing inventory
 * GuiWrappedInventory gui = new GuiWrappedInventory(player, existingInventory);
 * gui.draw().set(Slot.of(0), Button.of(controlItem));
 * gui.openGui();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/19
 * @see AbstractGuiCustom
 * @see GuiCustom
 */
public class GuiWrappedInventory extends AbstractGuiCustom<GuiWrappedInventory> {
    /**
     * The original inventory being wrapped by this GUI.
     */
    protected Inventory inventory;

    /**
     * Creates a new wrapped GUI for the specified player and inventory.
     * <p>
     * This constructor automatically disables most click restrictions to allow
     * more natural interaction with the wrapped inventory:
     * <ul>
     *     <li>cancelClickOther = false</li>
     *     <li>cancelClickBottom = false</li>
     *     <li>cancelMoveHotBarItemToSelf = false</li>
     *     <li>cancelMoveItemToSelf = false</li>
     *     <li>cancelMoveItemToBottom = false</li>
     * </ul>
     *
     *
     * @param player the player who will view this GUI
     * @param inventory the existing inventory to wrap
     */
    public GuiWrappedInventory(@NonNull Player player, Inventory inventory) {
        super(player);
        this.inventory = inventory;
        cancelClickOther = false;
        cancelClickBottom = false;
        cancelMoveHotBarItemToSelf = false;
        cancelMoveItemToSelf = false;
        cancelMoveItemToBottom = false;
    }

    /**
     * Creates a new wrapped GUI without a player or inventory.
     * <p>
     * The player and inventory must be set before opening the GUI.
     *
     */
    public GuiWrappedInventory() {
        super();
    }

    /**
     * Builds the inventory for this GUI using the wrapped inventory's properties.
     * <p>
     * Creates a new inventory with the same type, size, and title as the wrapped inventory.
     *
     *
     * @param holder the inventory holder (this GUI instance)
     * @return the newly created inventory
     */
    @Override
    protected @NonNull Inventory build(@NonNull InventoryHolder holder) {
        return InventoryUtil.createInventory(holder, inventory.getType(), inventory.getSize(), title);
    }

    /**
     * Fills the inventory with items from the wrapped inventory.
     * <p>
     * If the 'all' parameter is true, clears the inventory first before copying items.
     * This method copies all items from the original wrapped inventory into the GUI's inventory.
     *
     *
     * @param inventory the inventory to fill
     * @param all whether to clear the inventory first
     * @return this GuiWrappedInventory instance for method chaining
     */
    @Override
    protected @NonNull GuiWrappedInventory fillItems(@NonNull Inventory inventory, boolean all) {
        if (all) {
            inventory.clear();
        }
        updateInventory(inventory);
        return self();
    }

    /**
     * Refreshes specific slots in the GUI.
     * <p>
     * This method is deprecated for GuiWrappedInventory as it doesn't apply to wrapped inventories.
     * Use {@link #refresh(boolean)} instead.
     *
     *
     * @param slots the slots to refresh (ignored)
     * @return this GuiWrappedInventory instance
     * @deprecated Use {@link #refresh(boolean)} instead
     */
    @Deprecated
    @Override
    public @NonNull GuiWrappedInventory refresh(@NonNull Slots slots) {
        return self();
    }

    /**
     * Handles click events in the wrapped inventory.
     * <p>
     * This method synchronizes the clicked inventory state back to the wrapped inventory
     * both immediately and after 1 tick to ensure consistency, as Bukkit may update the
     * inventory after the click event is processed.
     *
     *
     * @param e the inventory click event
     */
    @Override
    public void onClick(@NonNull InventoryClickEvent e) {
        super.onClick(e);
        setToInventory();
        Schedulers.sync().runLater(this::setToInventory, 1);
    }

    /**
     * Synchronizes changes from the cache inventory back to the wrapped inventory.
     * <p>
     * This ensures that any modifications made during click events are properly
     * reflected in the original wrapped inventory.
     *
     */
    private void setToInventory() {
        if (cacheInventory == null) {
            return;
        }
        try {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, cacheInventory.getItem(i));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Copies all items from the wrapped inventory to the target inventory.
     * <p>
     * Used internally to initialize the GUI's inventory with the contents
     * of the wrapped inventory.
     *
     *
     * @param build the target inventory to copy items to
     */
    private void updateInventory(Inventory build) {
        for (int i = 0; i < inventory.getSize(); i++) {
            build.setItem(i, inventory.getItem(i));
        }
    }

    /**
     * Returns this instance for method chaining.
     *
     * @return this GuiWrappedInventory instance
     */
    @Override
    protected @NonNull GuiWrappedInventory self() {
        return this;
    }

    /**
     * Creates a deep copy of this GUI instance.
     *
     * @return a new GuiWrappedInventory instance with copied properties
     */
    @Override
    public GuiWrappedInventory copy() {
        return (GuiWrappedInventory) super.copy();
    }

    /**
     * Creates a new empty GUI instance of the same type.
     *
     * @return a new empty GuiWrappedInventory instance
     */
    @Override
    protected GuiWrappedInventory newGui() {
        return new GuiWrappedInventory();
    }

    /**
     * Copies all properties from this GUI to the target GUI instance.
     * <p>
     * In addition to copying standard GUI properties, this also copies
     * the reference to the wrapped inventory.
     *
     *
     * @param newGui the target GUI to copy properties to
     * @return the target GUI with copied properties
     */
    @Override
    protected GuiWrappedInventory copy(Object newGui) {
        GuiWrappedInventory copy = (GuiWrappedInventory) super.copy(newGui);
        copy.inventory = inventory;
        return copy;
    }
}
