package me.huanmeng.gui.api.layout;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GuiLayout {
    private final int rows;
    private final Map<Slot, Button> buttons;

    public GuiLayout(int rows, Map<Slot, Button> buttons) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be between 1 and 6");
        if (buttons == null) throw new NullPointerException("buttons");
        for (Slot slot : buttons.keySet()) {
            if (slot.index() >= rows * 9) throw new IllegalArgumentException("slot outside layout: " + slot);
        }
        this.rows = rows;
        this.buttons = Collections.unmodifiableMap(new LinkedHashMap<Slot, Button>(buttons));
    }

    public static GuiLayout empty(int rows) {
        return new GuiLayout(rows, Collections.emptyMap());
    }

    public int rows() { return rows; }
    public Map<Slot, Button> buttons() { return buttons; }
}
