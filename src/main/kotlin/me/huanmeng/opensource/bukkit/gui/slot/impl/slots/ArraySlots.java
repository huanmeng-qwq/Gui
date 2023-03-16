package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class ArraySlots implements Slots {
    private final Slot[] slots;

    public ArraySlots(Slot[] slots) {
        this.slots = slots;
    }

    @Override
    public <G extends AbstractGui<G>> Slot[] slots(G gui) {
        return slots;
    }
}
