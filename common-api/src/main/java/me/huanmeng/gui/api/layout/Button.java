package me.huanmeng.gui.api.layout;

import me.huanmeng.gui.api.ClickContext;
import me.huanmeng.gui.api.ClickResult;
import me.huanmeng.gui.api.ItemView;
import me.huanmeng.gui.api.Viewer;

/**
 * Platform-independent button identity and click behavior.
 */
public interface Button {
    ItemView display(Viewer viewer);

    default ClickResult click(ClickContext context) {
        return ClickResult.CANCEL;
    }
}
