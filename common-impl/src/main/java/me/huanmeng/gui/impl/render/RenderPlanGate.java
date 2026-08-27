package me.huanmeng.gui.impl.render;

import java.util.Objects;
import me.huanmeng.gui.api.render.RenderPlan;

/** Accepts render results monotonically by state version. */
public final class RenderPlanGate {
    private long latestVersion = -1L;

    public synchronized boolean accept(RenderPlan plan) {
        Objects.requireNonNull(plan, "plan");
        if (plan.version() <= latestVersion) return false;
        latestVersion = plan.version();
        return true;
    }

    public synchronized long latestVersion() {
        return latestVersion;
    }
}
