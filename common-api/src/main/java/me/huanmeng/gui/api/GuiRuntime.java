package me.huanmeng.gui.api;

import me.huanmeng.gui.api.thread.ThreadGuard;

public interface GuiRuntime extends AutoCloseable {
    GuiRegistry registry();

    GuiScheduler scheduler();

    Gui createGui();

    boolean isClosed();

    ThreadGuard threadGuard();

    ViewerFactory<?> viewerFactory();

    @Override
    void close();
}
