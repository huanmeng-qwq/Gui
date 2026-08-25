package me.huanmeng.gui.api.edit;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.GuiEditor;

public interface GuiEditExecutor extends AutoCloseable {
    CompletionStage<EditResult> submit(Consumer<GuiEditor> editor);
    EditResult executeNow(Consumer<GuiEditor> editor);

    @Override
    void close();
}
