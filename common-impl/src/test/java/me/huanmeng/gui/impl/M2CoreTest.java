package me.huanmeng.gui.impl;

import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.GuiLifecycleState;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.ButtonPriority;
import me.huanmeng.gui.api.layout.GuiLayout;
import me.huanmeng.gui.api.layout.Slot;
import me.huanmeng.gui.api.page.PageArea;
import me.huanmeng.gui.api.page.Pagination;
import me.huanmeng.gui.api.render.ButtonRenderOperation;
import me.huanmeng.gui.api.render.RenderPlan;
import me.huanmeng.gui.impl.edit.SerialGuiEditExecutor;
import me.huanmeng.gui.impl.layout.GuiLayoutBuilder;
import me.huanmeng.gui.impl.lifecycle.GuiLifecycle;
import me.huanmeng.gui.impl.model.MutableGuiModel;
import me.huanmeng.gui.impl.render.DefaultRenderPlanGenerator;
import me.huanmeng.gui.impl.render.RenderPlanGate;
import me.huanmeng.gui.impl.test.TestButton;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletionStage;
import org.junit.jupiter.api.Test;

final class M2CoreTest {
    @Test
    void lifecycleHasExplicitTransitions() {
        GuiLifecycle lifecycle = new GuiLifecycle();
        check(lifecycle.state() == GuiLifecycleState.NEW, "initial state");
        check(lifecycle.open(), "open from new");
        check(lifecycle.state() == GuiLifecycleState.OPEN, "open state");
        check(!lifecycle.open(), "repeated open");
        check(lifecycle.close(), "close open gui");
        check(lifecycle.state() == GuiLifecycleState.CLOSED, "closed state");
        check(lifecycle.open(), "reopen closed gui");
        check(lifecycle.dispose(), "dispose gui");
        check(lifecycle.state() == GuiLifecycleState.DISPOSED, "disposed state");
        check(!lifecycle.open(), "open disposed gui");
    }

    @Test
    void queuedEditsRunSeriallyAndClosePendingWork() {
        final Queue<Runnable> scheduled = new ArrayDeque<Runnable>();
        final int[] calls = {0};
        SerialGuiEditExecutor executor = new SerialGuiEditExecutor(scheduled::add, editor(calls));
        CompletionStage<EditResult> first = executor.submit(editor -> editor.refresh(RefreshPolicy.SLOT));
        CompletionStage<EditResult> second = executor.submit(editor -> editor.refresh(RefreshPolicy.SLOT));
        executor.close();
        check(first.toCompletableFuture().join() == EditResult.CLOSED, "first pending edit close");
        check(second.toCompletableFuture().join() == EditResult.CLOSED, "second pending edit close");
        scheduled.remove().run();
        check(calls[0] == 0, "closed edits are not applied");
    }

    @Test
    void layoutUsesValidatedZeroBasedSlots() {
        GuiLayout layout = GuiLayout.empty(3);
        check(layout.rows() == 3 && layout.buttons().isEmpty(), "empty layout");
        check(Slot.of(0).index() == 0, "zero based slot");
        boolean rejected = false;
        try {
            new GuiLayout(3, java.util.Collections.singletonMap(Slot.of(27), null));
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "layout rejects out of range slot");
    }

    @Test
    void paginationClampsPagesAndRejectsInvalidSize() {
        Pagination<Integer> pagination = new Pagination<Integer>(java.util.Arrays.asList(1, 2, 3), 2);
        check(pagination.pageCount() == 2, "page count");
        check(pagination.page(99).equals(java.util.Collections.singletonList(3)), "page clamp");
        check(pagination.page(0).equals(java.util.Arrays.asList(1, 2)), "first page clamp");
        boolean rejected = false;
        try {
            new Pagination<Integer>(java.util.Collections.emptyList(), 0);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "invalid page size");
    }

    @Test
    void pageAreaClampsAndCopiesRuntimeState() {
        PageArea<Integer> area = new PageArea<Integer>(
                java.util.Arrays.asList(Slot.of(0), Slot.of(1)), java.util.Arrays.asList(1, 2, 3));
        area.currentPage(99);
        check(area.currentPage() == 2, "page area clamp");
        PageArea<Integer> copy = area.copy();
        copy.currentPage(1);
        check(area.currentPage() == 2 && copy.currentPage() == 1, "page area copy isolation");
        area.items(java.util.Collections.singletonList(1));
        check(area.currentPage() == 1, "items clamp current page");
    }

    @Test
    void layoutBuilderResolvesPriorityAndRegistrationOrder() {
        Button low = TestButton.create("low");
        Button high = TestButton.create("high");
        GuiLayout layout = new GuiLayoutBuilder(1)
                .bind(Slot.of(0), low, ButtonPriority.DEFAULT)
                .bind(Slot.of(0), high, ButtonPriority.EDIT)
                .build();
        check(layout.buttons().get(Slot.of(0)) == high, "priority resolution");
    }

    @Test
    void modelChangesProduceVersionedButtonRenderPlan() {
        MutableGuiModel model = new MutableGuiModel();
        java.util.Map<Slot, Button> buttons = new java.util.LinkedHashMap<Slot, Button>();
        buttons.put(Slot.of(0), TestButton.create("diamond"));
        model.layout(new GuiLayout(1, buttons));
        me.huanmeng.gui.api.GuiSnapshot snapshot = model.snapshot(true);
        me.huanmeng.gui.api.render.RenderPlan plan = new DefaultRenderPlanGenerator().generate(snapshot);
        check(plan.version() == snapshot.version(), "plan version");
        check(plan.operations().size() == 1, "button operation count");
        check(plan.operations().get(0) instanceof ButtonRenderOperation, "button operation type");
        model.data("selected", "diamond");
        check(model.snapshot(true).version() > snapshot.version(), "model version increments");
    }

    @Test
    void renderPlanContainsOnlyChangedAndClearedSlots() {
        Button first = TestButton.create("first");
        Button replacement = TestButton.create("replacement");
        MutableGuiModel model = new MutableGuiModel();
        model.layout(new GuiLayout(1, java.util.Collections.singletonMap(Slot.of(0), first)));
        me.huanmeng.gui.api.GuiSnapshot before = model.snapshot(true);
        java.util.Map<Slot, Button> changed = new java.util.LinkedHashMap<Slot, Button>();
        changed.put(Slot.of(1), replacement);
        model.layout(new GuiLayout(1, changed));
        me.huanmeng.gui.api.render.RenderPlan plan = new DefaultRenderPlanGenerator()
                .generate(before, model.snapshot(true));
        check(plan.operations().size() == 2, "differential operation count");
        check(plan.operations().get(0) instanceof me.huanmeng.gui.api.render.ClearSlotRenderOperation,
                "removed slot is cleared");
        check(plan.operations().get(1) instanceof ButtonRenderOperation, "new slot is set");
    }

    @Test
    void unchangedLayoutProducesEmptyPlan() {
        Button button = TestButton.create("same");
        MutableGuiModel model = new MutableGuiModel();
        GuiLayout layout = new GuiLayout(1, java.util.Collections.singletonMap(Slot.of(0), button));
        model.layout(layout);
        me.huanmeng.gui.api.GuiSnapshot before = model.snapshot(true);
        model.data("unrelated", "value");
        me.huanmeng.gui.api.render.RenderPlan plan = new DefaultRenderPlanGenerator()
                .generate(before, model.snapshot(true));
        check(plan.operations().isEmpty(), "unchanged bindings are not rendered");
    }

    @Test
    void modelCopyDoesNotShareMutableDataState() {
        MutableGuiModel original = new MutableGuiModel();
        original.data("selected", "first");
        MutableGuiModel copy = original.copy();
        copy.data("selected", "second");
        check("first".equals(original.snapshot(false).data().get("selected")), "original copy data isolation");
        check("second".equals(copy.snapshot(false).data().get("selected")), "copied data mutation");
    }

    @Test
    void rowChangeRequiresInventoryRebuild() {
        MutableGuiModel model = new MutableGuiModel();
        model.layout(new GuiLayout(6, java.util.Collections.singletonMap(Slot.of(53), TestButton.create("old"))));
        me.huanmeng.gui.api.GuiSnapshot before = model.snapshot(true);
        model.layout(new GuiLayout(1, java.util.Collections.singletonMap(Slot.of(0), TestButton.create("new"))));
        me.huanmeng.gui.api.render.RenderPlan plan = new DefaultRenderPlanGenerator()
                .generate(before, model.snapshot(true));
        check(plan.inventoryRebuildRequired(), "row change rebuild");
        check(plan.operations().size() == 1 && plan.operations().get(0) instanceof ButtonRenderOperation,
                "rebuild only renders new valid slots");
        check(((ButtonRenderOperation) plan.operations().get(0)).slot().index() == 0,
                "rebuild does not clear removed out-of-range slot");
    }

    @Test
    void incrementalButtonEditingPreservesPriorityLayers() {
        MutableGuiModel model = new MutableGuiModel();
        Button base = TestButton.create("base");
        Button overlay = TestButton.create("overlay");
        model.rows(2)
                .button(Slot.of(10), base)
                .button(Slot.of(10), overlay, ButtonPriority.EDIT);
        check(model.snapshot(false).layout().rows() == 2, "incremental row editing");
        check(model.snapshot(false).layout().buttons().get(Slot.of(10)) == overlay,
                "higher priority binding is visible");
        model.removeButton(Slot.of(10), ButtonPriority.EDIT);
        check(model.snapshot(false).layout().buttons().get(Slot.of(10)) == base,
                "removing overlay reveals lower priority binding");
        model.removeButton(Slot.of(10));
        check(!model.snapshot(false).layout().buttons().containsKey(Slot.of(10)),
                "removing slot clears every priority layer");
    }

    @Test
    void explicitSlotRefreshOnlyRendersThatSlot() {
        MutableGuiModel model = new MutableGuiModel();
        model.button(0, TestButton.create("first"));
        model.button(1, TestButton.create("second"));
        me.huanmeng.gui.api.GuiSnapshot before = model.snapshot(true);
        model.refreshSlot(Slot.of(1));
        RenderPlan plan = new DefaultRenderPlanGenerator().generate(before, model.snapshot(true));
        check(plan.operations().size() == 1, "one dirty slot operation");
        check(((ButtonRenderOperation) plan.operations().get(0)).slot().equals(Slot.of(1)),
                "dirty slot is rendered");
    }

    @Test
    void renderPlanGateRejectsStaleResults() {
        RenderPlanGate gate = new RenderPlanGate();
        check(gate.accept(new RenderPlan(2L, java.util.Collections.emptyList())), "new plan accepted");
        check(!gate.accept(new RenderPlan(1L, java.util.Collections.emptyList())), "stale plan rejected");
        check(!gate.accept(new RenderPlan(2L, java.util.Collections.emptyList())), "duplicate plan rejected");
        check(gate.accept(new RenderPlan(3L, java.util.Collections.emptyList())), "newer plan accepted");
    }

    private static GuiEditor editor(final int[] calls) {
        return new GuiEditor() {
            @Override
            public GuiEditor refresh(RefreshPolicy policy) {
                calls[0]++;
                return this;
            }

            @Override
            public GuiEditor data(String key, Object value) {
                return this;
            }

            @Override
            public GuiEditor custom(java.util.function.Consumer<GuiEditor> operation) {
                return this;
            }
        };
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
