package me.huanmeng.gui.api;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public interface GuiAccessContext {
    CompletionStage<EditResult> editGui(Consumer<GuiEditor> editor);

    EditResult editGuiNow(Consumer<GuiEditor> editor);

    GuiSnapshot snapshot();
}
