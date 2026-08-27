package me.huanmeng.gui.impl.render;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;
import me.huanmeng.gui.api.render.ButtonRenderOperation;
import me.huanmeng.gui.api.render.ClearSlotRenderOperation;
import me.huanmeng.gui.api.render.RenderOperation;
import me.huanmeng.gui.api.render.RenderPlan;
import me.huanmeng.gui.api.render.RenderRequest;

public final class DefaultRenderPlanGenerator {
    public RenderPlan generate(GuiSnapshot snapshot) {
        return generate(null, snapshot);
    }

    public RenderPlan generate(GuiSnapshot previous, GuiSnapshot current) {
        if (current == null) throw new NullPointerException("current");
        boolean rebuild = previous != null && previous.layout().rows() != current.layout().rows();
        Map<Slot, Button> before = previous == null
                ? java.util.Collections.<Slot, Button>emptyMap() : previous.layout().buttons();
        Map<Slot, Button> after = current.layout().buttons();
        boolean forceVisible = current.refreshPolicy() == RefreshPolicy.IMMEDIATE
                || current.refreshPolicy() == RefreshPolicy.VISIBLE_SLOTS
                || current.refreshPolicy() == RefreshPolicy.PAGE_AREA;
        List<RenderOperation> operations = new ArrayList<RenderOperation>();
        if (rebuild) {
            for (Map.Entry<Slot, Button> entry : after.entrySet()) {
                operations.add(new ButtonRenderOperation(entry.getKey(), entry.getValue()));
            }
            return new RenderPlan(current.version(), true, operations);
        }
        Set<Slot> slots = new LinkedHashSet<Slot>();
        slots.addAll(before.keySet());
        slots.addAll(after.keySet());
        for (Slot slot : slots) {
            Button oldButton = before.get(slot);
            Button newButton = after.get(slot);
            boolean slotDirty = previous != null && !Objects.equals(
                    previous.slotVersions().get(slot), current.slotVersions().get(slot));
            if (newButton == null) {
                if (oldButton != null) operations.add(new ClearSlotRenderOperation(slot));
            } else if (forceVisible || slotDirty || !Objects.equals(oldButton, newButton)) {
                operations.add(new ButtonRenderOperation(slot, newButton));
            }
        }
        return new RenderPlan(current.version(), rebuild, operations);
    }

    public RenderPlan generate(RenderRequest request, GuiSnapshot previous, GuiSnapshot current) {
        if (request == null) throw new NullPointerException("request");
        if (request.stateVersion() != current.version()) return null;
        return generate(previous, current);
    }
}
