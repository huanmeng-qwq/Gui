package me.huanmeng.gui.api.layout;

import java.util.Objects;

public final class ButtonBinding {
    private final Slot slot;
    private final Button button;
    private final ButtonPriority priority;
    private final long order;

    public ButtonBinding(Slot slot, Button button, ButtonPriority priority, long order) {
        this.slot = Objects.requireNonNull(slot, "slot");
        this.button = Objects.requireNonNull(button, "button");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.order = order;
    }
    public Slot slot() { return slot; }
    public Button button() { return button; }
    public ButtonPriority priority() { return priority; }
    public long order() { return order; }
}
