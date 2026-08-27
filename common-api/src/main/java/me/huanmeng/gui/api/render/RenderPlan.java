package me.huanmeng.gui.api.render;

import java.util.Collections;
import java.util.List;

public final class RenderPlan {
    private final long version;
    private final boolean inventoryRebuildRequired;
    private final List<RenderOperation> operations;

    public RenderPlan(long version, List<RenderOperation> operations) {
        this(version, false, operations);
    }

    public RenderPlan(long version, boolean inventoryRebuildRequired, List<RenderOperation> operations) {
        if (version < 0L) throw new IllegalArgumentException("version must be non-negative");
        if (operations == null) throw new NullPointerException("operations");
        this.version = version;
        this.inventoryRebuildRequired = inventoryRebuildRequired;
        this.operations = Collections.unmodifiableList(new java.util.ArrayList<RenderOperation>(operations));
    }

    public long version() {
        return version;
    }

    public boolean inventoryRebuildRequired() {
        return inventoryRebuildRequired;
    }

    public List<RenderOperation> operations() {
        return operations;
    }
}
