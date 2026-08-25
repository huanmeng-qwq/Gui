package me.huanmeng.gui.bukkit.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.GuiScheduler;
import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.api.RefreshPolicy;

final class DefaultBukkitGui implements Gui {
    private final GuiScheduler scheduler;
    private volatile boolean closed;

    DefaultBukkitGui(GuiScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override public GuiScheduler scheduler() { return scheduler; }
    @Override public boolean isClosed() { return closed; }
    @Override public CompletionStage<EditResult> editGui(Consumer<GuiEditor> editor) {
        if (closed) return CompletableFuture.completedFuture(EditResult.CLOSED);
        return CompletableFuture.completedFuture(editGuiNow(editor));
    }
    @Override public EditResult editGuiNow(Consumer<GuiEditor> editor) {
        if (closed) return EditResult.CLOSED;
        if (editor == null) throw new NullPointerException("editor");
        editor.accept(NoOpEditor.INSTANCE);
        return EditResult.APPLIED;
    }
    @Override public GuiSnapshot snapshot() {
        final boolean open = !closed;
        return new GuiSnapshot() {
            @Override public long version() { return 0L; }
            @Override public boolean open() { return open; }
        };
    }
    @Override public void close() { closed = true; }

    private static final class NoOpEditor implements GuiEditor {
        private static final NoOpEditor INSTANCE = new NoOpEditor();
        @Override public GuiEditor refresh(RefreshPolicy policy) { return this; }
        @Override public GuiEditor data(String key, Object value) { return this; }
        @Override public GuiEditor custom(Consumer<GuiEditor> operation) { return this; }
    }
}
