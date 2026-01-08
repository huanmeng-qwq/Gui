package me.huanmeng.gui.button;

import me.huanmeng.gui.AbstractGui;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Data class containing all information about a button click event in a GUI.
 * <p>
 * This class encapsulates all relevant information when a player clicks on a button,
 * including the player, GUI, slot, button, and the underlying Bukkit inventory event details.
 * It serves as a comprehensive context object passed to button click handlers.
 *
 * <p>
 * All fields are public and final for efficient access. The class provides getter methods
 * for convenience and API consistency.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
public class ClickData {
    /**
     * The player who clicked the button.
     */
    public final Player player;

    /**
     * The GUI containing the button that was clicked.
     */
    public final AbstractGui<?> gui;

    /**
     * The slot that was clicked.
     */
    public final Slot slot;

    /**
     * The source slot if this is a move/swap operation, or null for regular clicks.
     */
    @Nullable
    public final Slot formSlot;

    /**
     * The button that was clicked, or null if the slot has no button.
     */
    @Nullable
    public final Button button;

    /**
     * The underlying Bukkit inventory click event.
     */
    public final InventoryClickEvent event;

    /**
     * The type of click (LEFT, RIGHT, SHIFT_LEFT, etc.).
     */
    public final ClickType click;

    /**
     * The inventory action (PICKUP_ALL, PLACE_ONE, MOVE_TO_OTHER_INVENTORY, etc.).
     */
    public final InventoryAction action;

    /**
     * The type of slot that was clicked (CONTAINER, ARMOR, CRAFTING, etc.).
     */
    public final InventoryType.SlotType slotType;

    /**
     * The raw slot index that was clicked.
     */
    public final int slotKey;

    /**
     * The hotbar key pressed, or -1 if no hotbar key was used.
     */
    public final int hotBarKey;

    /**
     * Constructs a new ClickData with all click event information.
     *
     * @param player the player who clicked
     * @param gui the GUI containing the button
     * @param slot the slot that was clicked
     * @param formSlot the source slot for move operations, or null
     * @param button the button that was clicked, or null
     * @param event the underlying Bukkit event
     * @param click the click type
     * @param action the inventory action
     * @param slotType the slot type
     * @param slotKey the raw slot index
     * @param hotBarKey the hotbar key, or -1
     */
    public ClickData(Player player, AbstractGui<?> gui, Slot slot, @Nullable Slot formSlot, @Nullable Button button, InventoryClickEvent event, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey) {
        this.player = player;
        this.gui = gui;
        this.slot = slot;
        this.formSlot = formSlot;
        this.button = button;
        this.event = event;
        this.click = click;
        this.action = action;
        this.slotType = slotType;
        this.slotKey = slotKey;
        this.hotBarKey = hotBarKey;
    }

    /**
     * Gets the player who clicked the button.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the GUI containing the button.
     *
     * @return the GUI
     */
    public AbstractGui<?> getGui() {
        return gui;
    }

    /**
     * Gets the slot that was clicked.
     *
     * @return the slot
     */
    public Slot getSlot() {
        return slot;
    }

    /**
     * Gets the source slot for move operations.
     *
     * @return the source slot, or null for regular clicks
     */
    public @Nullable Slot getFormSlot() {
        return formSlot;
    }

    /**
     * Gets the button that was clicked.
     *
     * @return the button, or null if the slot has no button
     */
    @Nullable
    public Button getButton() {
        return button;
    }

    /**
     * Gets the underlying Bukkit inventory click event.
     *
     * @return the inventory click event
     */
    public InventoryClickEvent getEvent() {
        return event;
    }

    /**
     * Gets the type of click.
     *
     * @return the click type
     */
    public ClickType getClick() {
        return click;
    }

    /**
     * Gets the inventory action.
     *
     * @return the inventory action
     */
    public InventoryAction getAction() {
        return action;
    }

    /**
     * Gets the slot type.
     *
     * @return the slot type
     */
    public InventoryType.SlotType getSlotType() {
        return slotType;
    }

    /**
     * Gets the raw slot index.
     *
     * @return the slot index
     */
    public int getSlotKey() {
        return slotKey;
    }

    /**
     * Gets the hotbar key that was pressed.
     *
     * @return the hotbar key, or -1 if no hotbar key was used
     */
    public int getHotBarKey() {
        return hotBarKey;
    }
}