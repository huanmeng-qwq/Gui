package me.huanmeng.gui.gui.slot.impl.slot;

import me.huanmeng.gui.gui.slot.Slot;

/**
 * Basic implementation of the {@link Slot} interface.
 * <p>
 * SlotImpl is the simplest Slot implementation that only stores a slot index
 * position. It provides default behavior for click handling and placement
 * validation as defined in the Slot interface.
 * <p>
 * This is the most commonly used Slot implementation and is returned by
 * factory methods like {@link Slot#of(int)}, {@link Slot#ofGame(int, int)},
 * and {@link Slot#ofBukkit(int, int)}.
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
public class SlotImpl implements Slot {
    /**
     * The zero-based slot index in the inventory.
     */
    private final int index;

    /**
     * Creates a new SlotImpl at the specified index.
     *
     * @param index the slot index (0-based)
     */
    public SlotImpl(int index) {
        this.index = index;
    }

    /**
     * Gets the slot index position in the inventory.
     *
     * @return the zero-based slot index
     */
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
