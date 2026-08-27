package me.huanmeng.gui.impl.edit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.edit.GuiEditExecutor;

/** Serializes edits and completes queued work as CLOSED when disposed. */
public final class SerialGuiEditExecutor implements GuiEditExecutor {
    private final Executor executor;
    private final GuiEditor editor;
    private final Deque<PendingEdit> queue = new ArrayDeque<PendingEdit>();
    private boolean draining;
    private boolean closed;

    public SerialGuiEditExecutor(Executor executor, GuiEditor editor) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.editor = Objects.requireNonNull(editor, "editor");
    }

    @Override
    public CompletionStage<EditResult> submit(Consumer<GuiEditor> operation) {
        Objects.requireNonNull(operation, "operation");
        PendingEdit pending = new PendingEdit(operation);
        boolean schedule = false;
        synchronized (this) {
            if (closed) return CompletableFuture.completedFuture(EditResult.CLOSED);
            queue.addLast(pending);
            if (!draining) {
                draining = true;
                schedule = true;
            }
        }
        if (schedule) executor.execute(this::drain);
        return pending.result;
    }

    private void drain() {
        for (;;) {
            PendingEdit pending;
            synchronized (this) {
                pending = queue.pollFirst();
                if (pending == null) {
                    draining = false;
                    return;
                }
                if (closed) {
                    pending.result.complete(EditResult.CLOSED);
                    continue;
                }
            }
            try {
                pending.operation.accept(editor);
                pending.result.complete(EditResult.APPLIED);
            } catch (Throwable error) {
                pending.result.completeExceptionally(error);
            }
        }
    }

    @Override
    public EditResult executeNow(Consumer<GuiEditor> operation) {
        Objects.requireNonNull(operation, "operation");
        synchronized (this) {
            if (closed) return EditResult.CLOSED;
        }
        operation.accept(editor);
        return EditResult.APPLIED;
    }

    @Override
    public void close() {
        synchronized (this) {
            if (closed) return;
            closed = true;
            PendingEdit pending;
            while ((pending = queue.pollFirst()) != null) {
                pending.result.complete(EditResult.CLOSED);
            }
        }
    }

    private static final class PendingEdit {
        private final Consumer<GuiEditor> operation;
        private final CompletableFuture<EditResult> result = new CompletableFuture<EditResult>();
        private PendingEdit(Consumer<GuiEditor> operation) { this.operation = operation; }
    }
}
