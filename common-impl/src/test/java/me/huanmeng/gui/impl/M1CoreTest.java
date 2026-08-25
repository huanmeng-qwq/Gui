package me.huanmeng.gui.impl;

import org.junit.jupiter.api.Test;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.state.DefaultState;
import me.huanmeng.gui.api.state.StateBinding;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import me.huanmeng.gui.api.thread.WrongThreadException;
import me.huanmeng.gui.api.thread.WrongThreadPolicy;
import me.huanmeng.gui.impl.edit.SerialGuiEditExecutor;
import me.huanmeng.gui.impl.state.StateBindingImpl;
import me.huanmeng.gui.impl.thread.DefaultThreadGuard;

/** Executable unit checks that require no third-party test framework. */
public final class M1CoreTest {
    @Test
    void stateNotifiesOnlyOnChangeAndUnsubscribes() {
        DefaultState<Integer> state = new DefaultState<Integer>(1);
        final int[] calls = {0};
        me.huanmeng.gui.api.state.Subscription subscription = state.subscribe((oldValue, newValue) -> calls[0]++);
        state.setValue(1);
        state.setValue(2);
        subscription.close();
        state.setValue(3);
        check(calls[0] == 1, "state notification semantics");
    }

    @Test
    void bindingClosesSubscription() {
        DefaultState<Integer> state = new DefaultState<Integer>(1);
        final int[] calls = {0};
        StateBinding<Integer> binding = new StateBindingImpl<Integer>(state, RefreshPolicy.SLOT,
                (oldValue, newValue) -> calls[0]++);
        state.setValue(2);
        binding.close();
        state.setValue(3);
        check(calls[0] == 1, "binding close semantics");
    }

    @Test
    void threadGuardHonorsThrowPolicy() throws Exception {
        final DefaultThreadGuard guard = new DefaultThreadGuard(
                new RuntimeSettings(false, WrongThreadPolicy.THROW), Thread.currentThread());
        final boolean[] thrown = {false};
        Thread thread = new Thread(() -> {
            try { guard.checkRenderThread(); }
            catch (WrongThreadException expected) { thrown[0] = true; }
        });
        thread.start();
        thread.join(TimeUnit.SECONDS.toMillis(2));
        check(thrown[0], "thread guard throw policy");
    }

    @Test
    void serialExecutorAppliesAndCloses() {
        final int[] calls = {0};
        Executor direct = Runnable::run;
        SerialGuiEditExecutor executor = new SerialGuiEditExecutor(direct,
                new me.huanmeng.gui.api.GuiEditor() {
                    public me.huanmeng.gui.api.GuiEditor refresh(RefreshPolicy policy) { calls[0]++; return this; }
                    public me.huanmeng.gui.api.GuiEditor data(String key, Object value) { return this; }
                    public me.huanmeng.gui.api.GuiEditor custom(java.util.function.Consumer<me.huanmeng.gui.api.GuiEditor> op) { return this; }
                });
        try {
            executor.submit(editor -> editor.refresh(RefreshPolicy.SLOT)).toCompletableFuture().join();
        } finally { executor.close(); }
        check(calls[0] == 1, "serial edit execution");
        check(executor.submit(editor -> { }).toCompletableFuture().join() == me.huanmeng.gui.api.EditResult.CLOSED,
                "closed edit execution");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
