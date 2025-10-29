package me.huanmeng.gui.gui.slot;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a slot position in the player's inventory.
 * <p>
 * PlayerSlot wraps a standard {@link Slot} to indicate that it references a position
 * in the player's personal inventory rather than the GUI inventory. This is used when
 * creating GUIs that need to interact with or reference the player's inventory slots.
 * <p>
 * Player inventory slots follow Minecraft's inventory indexing:
 * <ul>
 *     <li>Slots 0-8: Hotbar (bottom row visible to player)</li>
 *     <li>Slots 9-35: Main inventory (3 rows above hotbar)</li>
 * </ul>
 * <p>
 * When clicked, the slot properly handles the click event while maintaining the
 * context that this is a player inventory slot.
 *
 * @author huanmeng_qwq
 */
public class PlayerSlot implements Slot {
    /**
     * The wrapped slot that defines the position.
     */
    @NonNull
    private final Slot slot;

    /**
     * Creates a new PlayerSlot wrapping the specified slot.
     *
     * @param slot the slot to wrap as a player inventory slot
     */
    public PlayerSlot(@NonNull Slot slot) {
        this.slot = slot;
    }

    /**
     * Gets the slot index position in the player's inventory.
     *
     * @return the slot index (0-35 for player inventories)
     */
    @Override
    public int getIndex() {
        return slot.getIndex();
    }

    /**
     * Handles click events on this player inventory slot.
     * <p>
     * Delegates to the wrapped slot's click handler while ensuring the
     * ClickData properly reflects that this is a player slot.
     *
     * @param clickData the click event data
     * @return the result indicating whether to cancel the event
     */
    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        return this.slot.onClick(new ClickData(clickData.player, clickData.gui, slot, this, clickData.button, clickData.event, clickData.click, clickData.action, clickData.slotType, clickData.slotKey, clickData.hotBarKey));
    }

    /**
     * Determines whether a button can be placed on this player inventory slot.
     *
     * @param button the button attempting to be placed
     * @param player the player whose inventory this slot belongs to
     * @return true if the button can be placed, false otherwise
     */
    @Override
    public boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return slot.tryPlace(button, player);
    }

    /**
     * Returns this PlayerSlot instance.
     * <p>
     * Since this is already a PlayerSlot, it returns itself rather than creating
     * a new wrapper.
     *
     * @return this PlayerSlot instance
     */
    @Override
    public PlayerSlot asPlayer() {
        return this;
    }

    /**
     * Gets the wrapped slot that defines the position.
     *
     * @return the underlying Slot instance
     */
    public @NotNull Slot getSlot() {
        return slot;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSlot that = (PlayerSlot) o;
        return Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slot);
    }
}
