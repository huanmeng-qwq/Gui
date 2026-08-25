package me.huanmeng.gui.api.render;

import java.util.Collections;
import java.util.List;

public final class RenderPlan {
    private final long version;
    private final List<RenderOperation> operations;

    public RenderPlan(long version, List<RenderOperation> operations) {
        if (version < 0L) throw new IllegalArgumentException("version must be non-negative");
        this.version = version;
        this.operations = Collections.unmodifiableList(new java.util.ArrayList<RenderOperation>(operations));
    }

    public long version() { return version; }
    public List<RenderOperation> operations() { return operations; }
}
