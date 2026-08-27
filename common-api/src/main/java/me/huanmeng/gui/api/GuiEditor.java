package me.huanmeng.gui.api;

import java.util.function.Consumer;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.ButtonPriority;
import me.huanmeng.gui.api.layout.GuiLayout;
import me.huanmeng.gui.api.layout.Slot;

public interface GuiEditor {
    GuiEditor refresh(RefreshPolicy policy);

    default GuiEditor refreshSlot(Slot slot) {
        throw new UnsupportedOperationException("slot refresh is not supported");
    }

    GuiEditor data(String key, Object value);

    default GuiEditor removeData(String key) {
        throw new UnsupportedOperationException("data removal is not supported");
    }

    default GuiEditor rows(int rows) {
        throw new UnsupportedOperationException("row editing is not supported");
    }

    default GuiEditor button(int slot, Button button) {
        return button(Slot.of(slot), button);
    }

    default GuiEditor button(Slot slot, Button button) {
        return button(slot, button, ButtonPriority.DEFAULT);
    }

    default GuiEditor button(Slot slot, Button button, ButtonPriority priority) {
        throw new UnsupportedOperationException("button editing is not supported");
    }

    default GuiEditor removeButton(int slot) {
        return removeButton(Slot.of(slot));
    }

    default GuiEditor removeButton(Slot slot) {
        throw new UnsupportedOperationException("button editing is not supported");
    }

    default GuiEditor removeButton(Slot slot, ButtonPriority priority) {
        throw new UnsupportedOperationException("button editing is not supported");
    }

    default GuiEditor clearButtons() {
        throw new UnsupportedOperationException("button editing is not supported");
    }

    default GuiEditor layout(GuiLayout layout) { return this; }

    GuiEditor custom(Consumer<GuiEditor> operation);
}
