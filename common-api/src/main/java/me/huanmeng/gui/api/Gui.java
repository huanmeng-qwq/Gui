package me.huanmeng.gui.api;

import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;
import java.util.Collection;

/**
 * A single GUI instance managed by a {@link GuiRuntime}.
 */
public interface Gui extends GuiContext {
    GuiLifecycleState lifecycleState();

    boolean open();

    boolean closeGui();

    boolean dispose();

    void refresh();

    Collection<? extends Viewer> viewers();

    boolean open(Viewer viewer);

    boolean close(Viewer viewer);

    InteractionPolicy interactionPolicy();

    void interactionPolicy(InteractionPolicy policy);

    Button buttonAt(int slot);

    Button buttonAt(Slot slot);

    Gui copy();
}
