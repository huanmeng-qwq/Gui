package me.huanmeng.gui.api;

public interface GuiScheduler {
    void execute(Runnable task);
    void executeLater(Runnable task);
}
