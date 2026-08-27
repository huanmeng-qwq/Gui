package me.huanmeng.gui.api.render;

import java.util.Objects;
import me.huanmeng.gui.api.layout.Slot;

public final class ClearSlotRenderOperation implements RenderOperation {
    private final Slot slot;

    public ClearSlotRenderOperation(Slot slot) {
        this.slot = Objects.requireNonNull(slot, "slot");
    }

    @Override public String type() { return "clear-slot"; }
    public Slot slot() { return slot; }
}
