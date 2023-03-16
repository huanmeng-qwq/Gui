package me.huanmeng.opensource.bukkit.gui.slot.impl.slot;

import me.huanmeng.opensource.bukkit.gui.slot.Slot;

/**
 * 2022/6/17<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class SlotImpl implements Slot {
    private final int index;

    public SlotImpl(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlotImpl slot = (SlotImpl) o;

        return index == slot.index;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
