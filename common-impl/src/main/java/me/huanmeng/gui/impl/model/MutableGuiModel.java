package me.huanmeng.gui.impl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.ButtonBinding;
import me.huanmeng.gui.api.layout.ButtonPriority;
import me.huanmeng.gui.api.layout.GuiLayout;
import me.huanmeng.gui.api.layout.Slot;

/** Platform-independent mutable GUI state with layered button bindings. */
public final class MutableGuiModel implements GuiEditor {
    private int rows = 1;
    private final Map<Slot, EnumMap<ButtonPriority, ButtonBinding>> bindings =
            new LinkedHashMap<Slot, EnumMap<ButtonPriority, ButtonBinding>>();
    private final Map<Slot, Long> slotVersions = new LinkedHashMap<Slot, Long>();
    private final Map<String, Object> data = new LinkedHashMap<String, Object>();
    private long version;
    private long bindingOrder;
    private RefreshPolicy refreshPolicy = RefreshPolicy.BATCHED;

    @Override
    public synchronized GuiEditor refresh(RefreshPolicy policy) {
        refreshPolicy = Objects.requireNonNull(policy, "policy");
        version++;
        return this;
    }

    @Override
    public synchronized GuiEditor refreshSlot(Slot slot) {
        Objects.requireNonNull(slot, "slot");
        validateSlot(slot);
        markSlotChanged(slot);
        return this;
    }

    @Override
    public synchronized GuiEditor data(String key, Object value) {
        if (key == null) throw new NullPointerException("key");
        if (!Objects.equals(data.put(key, value), value)) {
            refreshPolicy = RefreshPolicy.BATCHED;
            version++;
        }
        return this;
    }

    @Override
    public synchronized GuiEditor removeData(String key) {
        if (key == null) throw new NullPointerException("key");
        if (data.containsKey(key)) {
            data.remove(key);
            refreshPolicy = RefreshPolicy.BATCHED;
            version++;
        }
        return this;
    }

    @Override
    public synchronized GuiEditor rows(int rows) {
        validateRows(rows);
        if (this.rows != rows) {
            for (Slot slot : bindings.keySet()) {
                if (slot.index() >= rows * 9) {
                    throw new IllegalArgumentException("slot outside resized layout: " + slot);
                }
            }
            this.rows = rows;
            refreshPolicy = RefreshPolicy.LAYOUT;
            version++;
        }
        return this;
    }

    @Override
    public synchronized GuiEditor button(Slot slot, Button button, ButtonPriority priority) {
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(button, "button");
        Objects.requireNonNull(priority, "priority");
        validateSlot(slot);
        EnumMap<ButtonPriority, ButtonBinding> layers = bindings.get(slot);
        if (layers == null) {
            layers = new EnumMap<ButtonPriority, ButtonBinding>(ButtonPriority.class);
            bindings.put(slot, layers);
        }
        layers.put(priority, new ButtonBinding(slot, button, priority, bindingOrder++));
        markSlotChanged(slot);
        return this;
    }

    @Override
    public synchronized GuiEditor removeButton(Slot slot) {
        Objects.requireNonNull(slot, "slot");
        if (bindings.remove(slot) != null) markSlotChanged(slot);
        return this;
    }

    @Override
    public synchronized GuiEditor removeButton(Slot slot, ButtonPriority priority) {
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(priority, "priority");
        EnumMap<ButtonPriority, ButtonBinding> layers = bindings.get(slot);
        if (layers != null && layers.remove(priority) != null) {
            if (layers.isEmpty()) bindings.remove(slot);
            markSlotChanged(slot);
        }
        return this;
    }

    @Override
    public synchronized GuiEditor clearButtons() {
        if (!bindings.isEmpty()) {
            ArrayList<Slot> changed = new ArrayList<Slot>(bindings.keySet());
            bindings.clear();
            version++;
            refreshPolicy = RefreshPolicy.VISIBLE_SLOTS;
            for (Slot slot : changed) slotVersions.put(slot, version);
        }
        return this;
    }

    @Override
    public synchronized GuiEditor layout(GuiLayout layout) {
        Objects.requireNonNull(layout, "layout");
        Map<Slot, Button> current = resolveButtons();
        if (rows != layout.rows() || !current.equals(layout.buttons())) {
            Set<Slot> changed = new LinkedHashSet<Slot>();
            changed.addAll(current.keySet());
            changed.addAll(layout.buttons().keySet());
            rows = layout.rows();
            bindings.clear();
            for (Map.Entry<Slot, Button> entry : layout.buttons().entrySet()) {
                EnumMap<ButtonPriority, ButtonBinding> layers =
                        new EnumMap<ButtonPriority, ButtonBinding>(ButtonPriority.class);
                layers.put(ButtonPriority.DEFAULT, new ButtonBinding(entry.getKey(), entry.getValue(),
                        ButtonPriority.DEFAULT, bindingOrder++));
                bindings.put(entry.getKey(), layers);
            }
            version++;
            refreshPolicy = RefreshPolicy.LAYOUT;
            for (Slot slot : changed) slotVersions.put(slot, version);
        }
        return this;
    }

    @Override
    public GuiEditor custom(java.util.function.Consumer<GuiEditor> operation) {
        Objects.requireNonNull(operation, "operation").accept(this);
        return this;
    }

    public synchronized GuiSnapshot snapshot(boolean open) {
        return new Snapshot(version, open, new GuiLayout(rows, resolveButtons()), data,
                refreshPolicy, slotVersions);
    }

    public synchronized MutableGuiModel copy() {
        MutableGuiModel copy = new MutableGuiModel();
        copy.rows = rows;
        for (Map.Entry<Slot, EnumMap<ButtonPriority, ButtonBinding>> entry : bindings.entrySet()) {
            copy.bindings.put(entry.getKey(), new EnumMap<ButtonPriority, ButtonBinding>(entry.getValue()));
        }
        copy.slotVersions.putAll(slotVersions);
        copy.data.putAll(data);
        copy.version = version;
        copy.bindingOrder = bindingOrder;
        copy.refreshPolicy = refreshPolicy;
        return copy;
    }

    private Map<Slot, Button> resolveButtons() {
        Map<Slot, Button> resolved = new LinkedHashMap<Slot, Button>();
        for (Map.Entry<Slot, EnumMap<ButtonPriority, ButtonBinding>> entry : bindings.entrySet()) {
            ButtonBinding winner = null;
            for (ButtonBinding candidate : entry.getValue().values()) {
                if (winner == null || candidate.priority().ordinal() > winner.priority().ordinal()
                        || (candidate.priority() == winner.priority() && candidate.order() > winner.order())) {
                    winner = candidate;
                }
            }
            if (winner != null) resolved.put(entry.getKey(), winner.button());
        }
        return resolved;
    }

    private void markSlotChanged(Slot slot) {
        version++;
        refreshPolicy = RefreshPolicy.SLOT;
        slotVersions.put(slot, version);
    }

    private void validateSlot(Slot slot) {
        if (slot.index() >= rows * 9) throw new IllegalArgumentException("slot outside layout: " + slot);
    }

    private static void validateRows(int rows) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be between 1 and 6");
    }

    private static final class Snapshot implements GuiSnapshot {
        private final long version;
        private final boolean open;
        private final GuiLayout layout;
        private final Map<String, Object> data;
        private final RefreshPolicy refreshPolicy;
        private final Map<Slot, Long> slotVersions;

        private Snapshot(long version, boolean open, GuiLayout layout, Map<String, Object> data,
                         RefreshPolicy refreshPolicy, Map<Slot, Long> slotVersions) {
            this.version = version;
            this.open = open;
            this.layout = layout;
            this.data = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(data));
            this.refreshPolicy = refreshPolicy;
            this.slotVersions = Collections.unmodifiableMap(new LinkedHashMap<Slot, Long>(slotVersions));
        }

        @Override public long version() { return version; }
        @Override public boolean open() { return open; }
        @Override public GuiLayout layout() { return layout; }
        @Override public RefreshPolicy refreshPolicy() { return refreshPolicy; }
        @Override public Map<String, Object> data() { return data; }
        @Override public Map<Slot, Long> slotVersions() { return slotVersions; }
    }
}
