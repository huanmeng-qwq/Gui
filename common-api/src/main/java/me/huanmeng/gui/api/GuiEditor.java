package me.huanmeng.gui.api;

import java.util.function.Consumer;

public interface GuiEditor {
    GuiEditor refresh(RefreshPolicy policy);
    GuiEditor data(String key, Object value);
    GuiEditor custom(Consumer<GuiEditor> operation);
}
