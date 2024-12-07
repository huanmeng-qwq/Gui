package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class ArraySlots implements Slots {
    @NonNull
    private final Slot[] slots;

    public ArraySlots(@NonNull Slot[] slots) {
        this.slots = slots;
    }

    @Override
    public <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui) {
        return slots;
    }

    public ArraySlots forward(int forwardSlot) {
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot.getIndex(), forwardSlot)).toArray(Slot[]::new));
    }

    public ArraySlots forward(Slot forwardSlot) {
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot.getIndex(), forwardSlot)).toArray(Slot[]::new));
    }

    public Slot first() {
        if (slots.length > 0) {
            return slots[0];
        }
        return null;
    }
}
