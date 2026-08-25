package me.huanmeng.gui.impl.edit;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.edit.GuiEditExecutor;

public final class SerialGuiEditExecutor implements GuiEditExecutor {
    private final Executor executor;
    private final GuiEditor editor;
    private volatile boolean closed;

    public SerialGuiEditExecutor(Executor executor, GuiEditor editor) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.editor = Objects.requireNonNull(editor, "editor");
    }

    @Override
    public CompletionStage<EditResult> submit(final Consumer<GuiEditor> operation) {
        Objects.requireNonNull(operation, "operation");
        if (closed) return CompletableFuture.completedFuture(EditResult.CLOSED);
        CompletableFuture<EditResult> result = new CompletableFuture<EditResult>();
        executor.execute(() -> {
            if (closed) result.complete(EditResult.CLOSED);
            else {
                try {
                    operation.accept(editor);
                    result.complete(EditResult.APPLIED);
                } catch (Throwable error) {
                    result.completeExceptionally(error);
                }
            }
        });
        return result;
    }

    @Override
    public EditResult executeNow(Consumer<GuiEditor> operation) {
        Objects.requireNonNull(operation, "operation");
        if (closed) return EditResult.CLOSED;
        operation.accept(editor);
        return EditResult.APPLIED;
    }

    @Override public void close() { closed = true; }
}
