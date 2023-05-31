package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

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
}
