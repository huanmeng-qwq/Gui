package me.huanmeng.gui;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import me.huanmeng.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Represents a button placed at a specific slot in a GUI.
 * <p>
 * This class associates a {@link Button} with a {@link Slot} position, creating a complete
 * GUI element that can be rendered and interacted with. It also tracks whether the button
 * is placed in the player's inventory or the GUI inventory.
 *
 * <p>
 * GuiButton instances are immutable in terms of their slot position, but the button and slot
 * can be replaced using setter methods.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
public final class GuiButton {

    /**
     * The slot where this button is positioned.
     */
    @NonNull
    private Slot slot;

    /**
     * The button implementation providing display and click behavior.
     */
    @NonNull
    private Button button;

    /**
     * Whether this button is in the player's inventory rather than the GUI inventory.
     */
    private boolean isPlayerInventory;

    /**
     * Constructs a new GuiButton with the specified slot and button.
     * <p>
     * If the button is null, it will be replaced with an empty button.
     * </p>
     *
     * @param slot the slot position
     * @param button the button implementation, or null for an empty button
     */
    public GuiButton(@NonNull Slot slot, @Nullable Button button) {
        this.slot = slot;
        this.button = button == null ? Button.empty() : button;
        this.isPlayerInventory = this.slot.isPlayer();
    }

    /**
     * Handles click events on this button.
     * <p>
     * Delegates the click event to the underlying slot, which in turn may delegate
     * to the button's click handler.
     * </p>
     *
     * @param clickData the click event data
     * @return the result indicating how to handle the event
     */
    @NonNull
    public Result onClick(@NonNull ClickData clickData) {
        return slot.onClick(clickData);
    }

    /**
     * Gets the raw slot index.
     *
     * @return the slot index
     */
    public int getIndex() {
        return slot.getIndex();
    }

    /**
     * Checks if this button can be placed for the specified player.
     * <p>
     * This delegates to the slot's placement logic, which may perform various checks.
     * </p>
     *
     * @param player the player to check
     * @return true if the button can be placed, false otherwise
     */
    public boolean canPlace(Player player) {
        return slot.tryPlace(getButton(), player);
    }

    /**
     * Gets the slot where this button is positioned.
     *
     * @return the slot
     */
    @NonNull
    public Slot getSlot() {
        return slot;
    }

    /**
     * Sets the slot where this button is positioned.
     * <p>
     * This will update the {@code isPlayerInventory} flag based on the new slot.
     * </p>
     *
     * @param slot the new slot position
     */
    public void setSlot(@NonNull Slot slot) {
        this.slot = slot;
    }

    /**
     * Gets the button implementation.
     *
     * @return the button
     */
    @NonNull
    public Button getButton() {
        return button;
    }

    /**
     * Sets the button implementation.
     *
     * @param button the new button
     */
    public void setButton(@NonNull Button button) {
        this.button = button;
    }

    /**
     * Checks if this button is in the player's inventory.
     *
     * @return true if in the player's inventory, false if in the GUI inventory
     */
    public boolean isPlayerInventory() {
        return isPlayerInventory;
    }

    /**
     * Checks equality based on slot and inventory type.
     * <p>
     * Two GuiButtons are considered equal if they have the same slot and both are
     * (or are not) in the player's inventory. The button implementation is not considered.
     * </p>
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuiButton guiButton = (GuiButton) o;
        return isPlayerInventory == guiButton.isPlayerInventory && Objects.equals(slot, guiButton.slot);
    }

    /**
     * Generates a hash code based on slot and inventory type.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(slot, isPlayerInventory);
    }
}
