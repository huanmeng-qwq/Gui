package me.huanmeng.gui.bukkit.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.GuiRegistry;

final class DefaultGuiRegistry implements GuiRegistry {
    private final Set<Gui> guis = new LinkedHashSet<Gui>();

    @Override public synchronized Collection<? extends Gui> all() {
        return Collections.unmodifiableList(new ArrayList<Gui>(guis));
    }
    @Override public synchronized int size() { return guis.size(); }
    @Override public synchronized boolean register(Gui gui) {
        if (gui == null) throw new NullPointerException("gui");
        return guis.add(gui);
    }
    @Override public synchronized boolean unregister(Gui gui) {
        return guis.remove(gui);
    }
    @Override public void closeAll() {
        for (Gui gui : all()) {
            gui.close();
            unregister(gui);
        }
    }
}
