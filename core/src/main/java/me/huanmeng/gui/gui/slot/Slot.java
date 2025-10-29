package me.huanmeng.gui.gui.slot;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.gui.SlotUtil;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import me.huanmeng.gui.gui.slot.function.ButtonClickInterface;
import me.huanmeng.gui.gui.slot.function.ButtonPlaceInterface;
import me.huanmeng.gui.gui.slot.function.ButtonSimpleClickInterface;
import me.huanmeng.gui.gui.slot.impl.slot.SlotForward;
import me.huanmeng.gui.gui.slot.impl.slot.SlotImpl;
import me.huanmeng.gui.gui.slot.impl.slot.SlotInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * Represents a single slot position in a GUI inventory.
 * <p>
 * A Slot defines a specific position in a Minecraft inventory GUI and can handle
 * click events, placement validation, and item display. Slots can be created with
 * various configurations including custom click handlers, placement validators,
 * and forwarding behavior.
 * <p>
 * Slot indices start at 0 and follow Bukkit's inventory slot numbering:
 * <ul>
 *     <li>Row 0, Column 0 = Slot 0</li>
 *     <li>Row 0, Column 8 = Slot 8</li>
 *     <li>Row 1, Column 0 = Slot 9</li>
 *     <li>etc.</li>
 * </ul>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("rawtypes")
public interface Slot {
    /**
     * Gets the slot index position in the inventory.
     * <p>
     * Slot indices start at 0 and increment left-to-right, top-to-bottom.
     * For example, in a 3-row inventory:
     * <ul>
     *     <li>Top-left corner = 0</li>
     *     <li>Top-right corner = 8</li>
     *     <li>Middle-left = 9</li>
     *     <li>Bottom-right = 26</li>
     * </ul>
     *
     * @return the zero-based slot index
     */
    int getIndex();

    /**
     * Handles click events on this slot.
     * <p>
     * By default, delegates to the button's onClick handler. Implementations can
     * override this to provide custom click behavior.
     *
     * @param clickData the click event data containing player, button, and event information
     * @return the result indicating whether to cancel the event
     * @see Result
     */
    @NonNull
    default Result onClick(@NonNull ClickData clickData) {
        return clickData.button.onClick(clickData);
    }

    /**
     * Determines whether a {@link Button} can be placed on this slot.
     * <p>
     * This method is called before placing a button to validate if the placement
     * is allowed. By default, all placements are allowed.
     *
     * @param button the button attempting to be placed
     * @param player the player for whom the button is being placed
     * @return true if the button can be placed, false otherwise
     */
    default boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return true;
    }

    /**
     * Creates a simple slot at the specified index.
     * <p>
     * Slot indices start at 0.
     *
     * @param slotIndex the slot index (0-based)
     * @return a new Slot instance
     */
    @Contract(value = "_ -> new", pure = true)
    static Slot of(int slotIndex) {
        return new SlotImpl(slotIndex);
    }

    /**
     * Creates a slot from row and column coordinates (1-indexed).
     * <p>
     * Both row and column start at 1. For example:
     * <ul>
     *     <li>ofGame(1, 1) = slot 0 (top-left)</li>
     *     <li>ofGame(1, 9) = slot 8 (top-right)</li>
     *     <li>ofGame(2, 1) = slot 9 (second row, first column)</li>
     * </ul>
     *
     * @param row the row number (1-based)
     * @param column the column number (1-based)
     * @return a new Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofGame(int row, int column) {
        return new SlotImpl(SlotUtil.getSlot(row, column));
    }

    /**
     * Creates a slot from row and column coordinates (0-indexed, Bukkit style).
     * <p>
     * Both row and column start at 0. For example:
     * <ul>
     *     <li>ofBukkit(0, 0) = slot 0 (top-left)</li>
     *     <li>ofBukkit(0, 8) = slot 8 (top-right)</li>
     *     <li>ofBukkit(1, 0) = slot 9 (second row, first column)</li>
     * </ul>
     *
     * @param row the row number (0-based)
     * @param column the column number (0-based)
     * @return a new Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofBukkit(int row, int column) {
        return new SlotImpl(SlotUtil.getSlot(row + 1, column + 1));
    }

    /**
     * Creates a slot with a custom click handler.
     *
     * @param slotIndex the slot index (0-based)
     * @param buttonClickInterface the click handler to execute when this slot is clicked
     * @return a new Slot instance with the custom click handler
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot of(int slotIndex, ButtonClickInterface buttonClickInterface) {
        return new SlotInterface(slotIndex, buttonClickInterface);
    }

    /**
     * Creates a slot with a simplified click handler.
     *
     * @param slotIndex the slot index (0-based)
     * @param buttonSimpleClickInterface the simplified click handler
     * @return a new Slot instance with the custom click handler
     */
    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int slotIndex, ButtonSimpleClickInterface buttonSimpleClickInterface) {
        return new SlotInterface(slotIndex, buttonSimpleClickInterface);
    }

    /**
     * Creates a slot with a custom placement validator.
     *
     * @param slotIndex the slot index (0-based)
     * @param placeInterface the placement validator to check if buttons can be placed
     * @return a new Slot instance with the custom placement validator
     */
    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int slotIndex, ButtonPlaceInterface placeInterface) {
        return new SlotInterface(slotIndex, placeInterface);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     * <p>
     * When clicked, this slot will forward the click event to the specified forward slot.
     * Optionally accepts a placement validator.
     *
     * @param slotIndex the slot index for this slot (0-based)
     * @param forwardSlot the target slot index to forward clicks to
     * @param placeInterface optional placement validator (can be null)
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slotIndex, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slotIndex), Slot.of(forwardSlot), placeInterface);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slotIndex the slot index for this slot (0-based)
     * @param forwardSlot the target slot index to forward clicks to
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slotIndex, int forwardSlot) {
        return new SlotForward(Slot.of(slotIndex), Slot.of(forwardSlot));
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slotIndex the slot index for this slot (0-based)
     * @param forwardSlot the target Slot instance to forward clicks to
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slotIndex, @NonNull Slot forwardSlot) {
        return new SlotForward(Slot.of(slotIndex), forwardSlot);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slotIndex the slot index for this slot (0-based)
     * @param forwardSlot the target Slot instance to forward clicks to
     * @param placeInterface optional placement validator (can be null)
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slotIndex, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slotIndex), forwardSlot, placeInterface);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slot the source Slot instance
     * @param forwardSlot the target slot index to forward clicks to
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot) {
        return new SlotForward(slot, Slot.of(forwardSlot));
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slot the source Slot instance
     * @param forwardSlot the target slot index to forward clicks to
     * @param placeInterface optional placement validator (can be null)
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(slot, Slot.of(forwardSlot), placeInterface);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slot the source Slot instance
     * @param forwardSlot the target Slot instance to forward clicks to
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, @NonNull Slot forwardSlot) {
        return new SlotForward(slot, forwardSlot);
    }

    /**
     * Creates a forwarding slot that redirects clicks to another slot.
     *
     * @param slot the source Slot instance
     * @param forwardSlot the target Slot instance to forward clicks to
     * @param placeInterface optional placement validator (can be null)
     * @return a new forwarding Slot instance
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(slot, forwardSlot, placeInterface);
    }

    /**
     * Gets the ItemStack currently displayed in this slot for a specific player.
     * <p>
     * This retrieves the item shown by the button at this slot position.
     *
     * @param gui the GUI containing this slot
     * @param player the player viewing the GUI
     * @return the displayed ItemStack, or null if no button is at this slot
     */
    @Nullable
    default ItemStack getShowingItem(@NonNull AbstractGui<?> gui, @NonNull Player player) {
        GuiButton button = gui.getButton(this);
        if (button == null) return null;
        return button.getButton().getShowItem(player);
    }

    /**
     * Creates a player inventory slot at the specified index.
     * <p>
     * Player inventory slots reference positions in the player's own inventory
     * rather than the GUI inventory.
     *
     * @param slotIndex the slot index in the player's inventory (0-based)
     * @return a new PlayerSlot instance
     */
    static PlayerSlot player(int slotIndex) {
        return new PlayerSlot(Slot.of(slotIndex));
    }

    /**
     * Converts this slot to a PlayerSlot.
     * <p>
     * This wraps the current slot to indicate it references the player's inventory.
     *
     * @return a PlayerSlot wrapping this slot
     */
    default PlayerSlot asPlayer() {
        return new PlayerSlot(this);
    }

    /**
     * Checks if this slot represents a player inventory slot.
     *
     * @return true if this is a PlayerSlot instance, false otherwise
     */
    default boolean isPlayer() {
        return this instanceof PlayerSlot;
    }
}
