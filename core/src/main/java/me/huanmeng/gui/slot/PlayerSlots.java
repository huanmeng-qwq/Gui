package me.huanmeng.gui.slot;

import me.huanmeng.gui.AbstractGui;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents multiple slot positions in the player's inventory.
 * <p>
 * PlayerSlots wraps a standard {@link Slots} to indicate that all slots reference
 * positions in the player's personal inventory rather than the GUI inventory.
 * This is used when creating GUIs that need to interact with multiple slots in
 * the player's inventory.
 * <p>
 * When resolved for a GUI, this automatically converts all wrapped slots to
 * {@link PlayerSlot} instances, ensuring proper handling of player inventory
 * interactions.
 *
 * @author huanmeng_qwq
 * @since 2024/12/28
 */
public class PlayerSlots implements Slots {
    /**
     * The wrapped slots that define the positions.
     */
    @NonNull
    private final Slots slots;

    /**
     * Creates a new PlayerSlots wrapping the specified slots.
     *
     * @param slots the slots to wrap as player inventory slots
     */
    public PlayerSlots(@NotNull Slots slots) {
        this.slots = slots;
    }

    /**
     * Resolves this PlayerSlots into an array of PlayerSlot instances for a specific GUI.
     * <p>
     * All slots from the wrapped Slots are converted to PlayerSlot instances,
     * indicating they reference the player's inventory.
     *
     * @param gui the GUI to resolve slots for
     * @param <G> the GUI type
     * @return array of PlayerSlot instances
     */
    @Override
    public @NotNull <G extends AbstractGui<@NonNull G>> @NonNull Slot[] slots(@NonNull G gui) {
        return Arrays.stream(slots.slots(gui)).map(Slot::asPlayer).toArray(PlayerSlot[]::new);
    }

    /**
     * Returns this PlayerSlots instance.
     * <p>
     * Since this is already a PlayerSlots, it returns itself rather than creating
     * a new wrapper.
     *
     * @return this PlayerSlots instance
     */
    @Override
    public PlayerSlots asPlayer() {
        return this;
    }

    /**
     * Gets the wrapped slots that define the positions.
     *
     * @return the underlying Slots instance
     */
    public @NotNull Slots getSlots() {
        return slots;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSlots that = (PlayerSlots) o;
        return Objects.equals(slots, that.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slots);
    }
}
