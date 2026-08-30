package me.huanmeng.gui.api.layout;

public final class Slot {
    private final int index;

    private Slot(int index) {
        this.index = index;
    }

    public static Slot of(int index) {
        if (index < 0) throw new IllegalArgumentException("index must be non-negative");
        return new Slot(index);
    }

    public int index() {
        return index;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Slot && ((Slot) other).index == index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return "Slot(" + index + ")";
    }
}
