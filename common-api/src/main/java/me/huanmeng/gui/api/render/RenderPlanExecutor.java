package me.huanmeng.gui.api.render;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface RenderPlanExecutor {
    CompletionStage<RenderPlan> compute(RenderRequest request);
}
