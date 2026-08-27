package me.huanmeng.gui.api;

import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;

public interface ClickContext {
    Gui gui();
    Viewer viewer();
    Slot slot();
    Button button();
}
