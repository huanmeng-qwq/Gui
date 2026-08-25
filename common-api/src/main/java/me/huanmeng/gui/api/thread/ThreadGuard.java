package me.huanmeng.gui.api.thread;

public interface ThreadGuard {
    void checkRenderThread();
    boolean isRenderThread();
}
