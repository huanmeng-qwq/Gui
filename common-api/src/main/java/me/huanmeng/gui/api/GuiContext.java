package me.huanmeng.gui.api;

public interface GuiContext extends GuiAccessContext, AutoCloseable {
    GuiScheduler scheduler();
    boolean isClosed();

    @Override
    void close();
}
