package me.huanmeng.gui.impl.lifecycle;

import me.huanmeng.gui.api.GuiLifecycleState;

public final class GuiLifecycle {
    private GuiLifecycleState state = GuiLifecycleState.NEW;

    public synchronized GuiLifecycleState state() { return state; }

    public synchronized boolean open() {
        if (state == GuiLifecycleState.OPEN) return false;
        if (state != GuiLifecycleState.NEW && state != GuiLifecycleState.CLOSED) return false;
        state = GuiLifecycleState.OPENING;
        state = GuiLifecycleState.OPEN;
        return true;
    }

    public synchronized boolean close() {
        if (state == GuiLifecycleState.CLOSED || state == GuiLifecycleState.DISPOSED) return false;
        if (state == GuiLifecycleState.NEW) {
            state = GuiLifecycleState.CLOSED;
            return true;
        }
        state = GuiLifecycleState.CLOSING;
        state = GuiLifecycleState.CLOSED;
        return true;
    }

    public synchronized boolean dispose() {
        if (state == GuiLifecycleState.DISPOSED) return false;
        state = GuiLifecycleState.DISPOSED;
        return true;
    }
}
