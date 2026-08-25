package me.huanmeng.gui.api;

import java.util.Collection;

public interface GuiRegistry {
    Collection<? extends Gui> all();
    int size();
    boolean register(Gui gui);
    boolean unregister(Gui gui);
    void closeAll();
}
