package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * 2024/12/28<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class PlayerSlots implements Slots {
    @NonNull
    private final Slots slots;

    public PlayerSlots(@NotNull Slots slots) {
        this.slots = slots;
    }

    @Override
    public @NotNull <G extends AbstractGui<@NonNull G>> @NonNull Slot[] slots(@NonNull G gui) {
        return Arrays.stream(slots.slots(gui)).map(Slot::asPlayer).toArray(PlayerSlot[]::new);
    }

    @Override
    public PlayerSlots asPlayer() {
        return this;
    }

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
