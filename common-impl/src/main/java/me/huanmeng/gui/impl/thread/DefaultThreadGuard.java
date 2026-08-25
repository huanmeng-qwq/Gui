package me.huanmeng.gui.impl.thread;

import java.util.Objects;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import me.huanmeng.gui.api.thread.ThreadGuard;
import me.huanmeng.gui.api.thread.WrongThreadException;
import me.huanmeng.gui.api.thread.WrongThreadPolicy;

public final class DefaultThreadGuard implements ThreadGuard {
    private final RuntimeSettings settings;
    private final Thread renderThread;
    private volatile boolean disabled;

    public DefaultThreadGuard(RuntimeSettings settings) {
        this(settings, Thread.currentThread());
    }

    public DefaultThreadGuard(RuntimeSettings settings, Thread renderThread) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.renderThread = Objects.requireNonNull(renderThread, "renderThread");
    }

    @Override public boolean isRenderThread() { return disabled || Thread.currentThread() == renderThread; }

    @Override public void checkRenderThread() {
        if (isRenderThread()) return;
        WrongThreadPolicy policy = settings.wrongThreadPolicy();
        if (policy == WrongThreadPolicy.DISABLE_RENDER_THREAD) {
            disabled = true;
            return;
        }
        if (policy == WrongThreadPolicy.WARN) {
            System.err.println("GUI operation executed outside the render thread: " + Thread.currentThread().getName());
            return;
        }
        throw new WrongThreadException("GUI operation must run on " + renderThread.getName());
    }
}
