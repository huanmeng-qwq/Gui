package me.huanmeng.gui.gui.slot.impl.slot;

import me.huanmeng.gui.gui.slot.Slot;

/**
 * 2023/3/17<br>
 * Gui<br>
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
