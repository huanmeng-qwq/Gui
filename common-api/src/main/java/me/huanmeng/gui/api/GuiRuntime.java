package me.huanmeng.gui.api;

public interface GuiRuntime extends AutoCloseable {
    GuiRegistry registry();
    GuiScheduler scheduler();
    Gui createGui();
    boolean isClosed();

    @Override
    void close();
}
