package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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

    public Slots getSlots() {
        return slots;
    }
}
