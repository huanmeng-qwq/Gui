package me.huanmeng.gui.api;

import me.huanmeng.gui.api.layout.GuiLayout;
import me.huanmeng.gui.api.layout.Slot;
import java.util.Collections;
import java.util.Map;

public interface GuiSnapshot {
    long version();

    boolean open();

    default GuiLayout layout() {
        return GuiLayout.empty(1);
    }

    default RefreshPolicy refreshPolicy() {
        return RefreshPolicy.BATCHED;
    }

    default Map<String, Object> data() {
        return Collections.emptyMap();
    }

    /** Per-slot revisions used to refresh a slot even when its Button identity is unchanged. */
    default Map<Slot, Long> slotVersions() {
        return Collections.emptyMap();
    }
}
