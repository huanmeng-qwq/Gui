package me.huanmeng.gui.gui.slot.impl.slots;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import me.huanmeng.gui.gui.slot.function.ButtonPlaceInterface;
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
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot, forwardSlot)).toArray(Slot[]::new));
    }

    public ArraySlots forward(int forwardSlot, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot, forwardSlot, buttonPlaceInterface)).toArray(Slot[]::new));
    }

    public ArraySlots forward(Slot forwardSlot) {
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot, forwardSlot)).toArray(Slot[]::new));
    }

    public ArraySlots forward(Slot forwardSlot, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        return Slots.of(Arrays.stream(slots).map(slot -> Slot.forward(slot, forwardSlot, buttonPlaceInterface)).toArray(Slot[]::new));
    }

    public Slot first() {
        if (slots.length > 0) {
            return slots[0];
        }
        return null;
    }

    public @NonNull Slot[] slots() {
        return slots;
    }
}
