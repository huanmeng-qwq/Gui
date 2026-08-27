package me.huanmeng.gui.impl.layout;

import java.util.LinkedHashMap;
import java.util.Map;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.ButtonBinding;
import me.huanmeng.gui.api.layout.ButtonPriority;
import me.huanmeng.gui.api.layout.GuiLayout;
import me.huanmeng.gui.api.layout.Slot;

public final class GuiLayoutBuilder {
    private final int rows;
    private final Map<Slot, ButtonBinding> bindings = new LinkedHashMap<Slot, ButtonBinding>();
    private long order;

    public GuiLayoutBuilder(int rows) { this.rows = rows; }

    public GuiLayoutBuilder bind(Slot slot, Button button, ButtonPriority priority) {
        ButtonBinding candidate = new ButtonBinding(slot, button, priority, order++);
        ButtonBinding previous = bindings.get(slot);
        if (previous == null || candidate.priority().ordinal() > previous.priority().ordinal()
                || (candidate.priority() == previous.priority() && candidate.order() > previous.order())) {
            bindings.put(slot, candidate);
        }
        return this;
    }

    public GuiLayout build() {
        Map<Slot, Button> buttons = new LinkedHashMap<Slot, Button>();
        for (ButtonBinding binding : bindings.values()) buttons.put(binding.slot(), binding.button());
        return new GuiLayout(rows, buttons);
    }
}
