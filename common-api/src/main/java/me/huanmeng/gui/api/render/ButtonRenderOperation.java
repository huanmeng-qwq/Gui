package me.huanmeng.gui.api.render;

import java.util.Objects;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;

public final class ButtonRenderOperation implements RenderOperation {
    private final Slot slot;
    private final Button button;

    public ButtonRenderOperation(Slot slot, Button button) {
        this.slot = Objects.requireNonNull(slot, "slot");
        this.button = Objects.requireNonNull(button, "button");
    }

    @Override public String type() { return "button"; }
    public Slot slot() { return slot; }
    public Button button() { return button; }
}
