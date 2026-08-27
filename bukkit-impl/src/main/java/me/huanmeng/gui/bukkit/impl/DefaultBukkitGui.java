package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.EditResult;
import me.huanmeng.gui.api.GuiEditor;
import me.huanmeng.gui.api.GuiLifecycleState;
import me.huanmeng.gui.api.GuiScheduler;
import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.Viewer;
import me.huanmeng.gui.api.layout.Slot;
import me.huanmeng.gui.api.render.RenderPlan;
import me.huanmeng.gui.bukkit.api.BukkitButton;
import me.huanmeng.gui.bukkit.api.BukkitGui;
import me.huanmeng.gui.bukkit.api.BukkitInteractionPolicy;
import me.huanmeng.gui.bukkit.api.BukkitViewer;
import me.huanmeng.gui.impl.edit.SerialGuiEditExecutor;
import me.huanmeng.gui.impl.model.MutableGuiModel;
import me.huanmeng.gui.impl.render.DefaultRenderPlanGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

final class DefaultBukkitGui implements BukkitGui {
    private final GuiScheduler scheduler;
    private final DefaultBukkitGuiRuntime runtime;
    private final me.huanmeng.gui.impl.lifecycle.GuiLifecycle lifecycle =
            new me.huanmeng.gui.impl.lifecycle.GuiLifecycle();
    private final Runnable onDisposed;
    private final MutableGuiModel model = new MutableGuiModel();
    private final DefaultRenderPlanGenerator planGenerator = new DefaultRenderPlanGenerator();
    private final BukkitInventoryApplier inventoryApplier;
    private final Map<UUID, BukkitViewerSession> sessions =
            new LinkedHashMap<UUID, BukkitViewerSession>();
    private volatile SerialGuiEditExecutor editExecutor;
    private volatile Player configuredPlayer;
    private volatile String title = "";
    private volatile BukkitInteractionPolicy interactionPolicy =
            BukkitInteractionPolicy.defaults();

    DefaultBukkitGui(DefaultBukkitGuiRuntime runtime, GuiScheduler scheduler, Runnable onDisposed) {
        this.runtime = runtime;
        this.scheduler = scheduler;
        this.onDisposed = onDisposed;
        this.inventoryApplier = new BukkitInventoryApplier(runtime.inventories(), runtime.items(), runtime.viewerFactory());
        this.editExecutor = new SerialGuiEditExecutor(scheduler::execute, model);
    }

    @Override
    public GuiScheduler scheduler() {
        return scheduler;
    }

    @Override
    public Player player() {
        Player configured = configuredPlayer;
        if (configured != null) return configured;
        synchronized (this) {
            return sessions.isEmpty() ? null : sessions.values().iterator().next().player();
        }
    }

    @Override
    public void player(Player player) {
        this.configuredPlayer = player;
    }

    @Override
    public Collection<Player> players() {
        synchronized (this) {
            Collection<Player> result = new ArrayList<Player>();
            for (BukkitViewerSession session : sessions.values()) result.add(session.player());
            return Collections.unmodifiableCollection(result);
        }
    }

    @Override
    public void open(Player player) {
        if (player == null) throw new NullPointerException("player");
        scheduler.execute(() -> openOnMainThread(player));
    }

    private synchronized void openOnMainThread(Player player) {
        runtime.threadGuard().checkRenderThread();
        if (lifecycle.state() == GuiLifecycleState.DISPOSED) return;
        if (lifecycle.state() == GuiLifecycleState.NEW || lifecycle.state() == GuiLifecycleState.CLOSED) {
            lifecycle.open();
            editExecutor = new SerialGuiEditExecutor(scheduler::execute, model);
            model.refresh(RefreshPolicy.LAYOUT);
        }
        UUID id = player.getUniqueId();
        Inventory inventory = runtime.inventories().createChest(
                this, model.snapshot(false).layout().rows(), title);
        BukkitViewerSession session = new BukkitViewerSession(runtime.viewerFactory().create(player), inventory);
        sessions.put(id, session);
        applyPlan(session, null, model.snapshot(true));
        player.openInventory(session.inventory());
    }

    @Override
    public void close(Player player) {
        if (player != null) scheduler.execute(() -> closeSession(player, true));
    }

    void handleInventoryClosed(Player player, Inventory closedInventory) {
        synchronized (this) {
            if (inventory(player) == closedInventory) closeSession(player, false);
        }
    }

    private synchronized void closeSession(Player player, boolean closeInventory) {
        runtime.threadGuard().checkRenderThread();
        UUID id = player.getUniqueId();
        sessions.remove(id);
        if (configuredPlayer != null && configuredPlayer.getUniqueId().equals(id)) configuredPlayer = null;
        if (closeInventory && player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getHolder() == this) player.closeInventory();
        if (sessions.isEmpty() && lifecycle.state() == GuiLifecycleState.OPEN) closeGui();
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public void title(String title) {
        this.title = title == null ? "" : title;
        reopenForTitleChange();
    }

    @Override
    public void title(Component title) {
        if (title == null) throw new NullPointerException("title");
        this.title = LegacyComponentSerializer.legacySection().serialize(title);
        reopenForTitleChange();
    }

    private void reopenForTitleChange() {
        model.refresh(RefreshPolicy.TITLE);
        scheduler.execute(() -> {
            for (Player viewer : new ArrayList<Player>(players())) openOnMainThread(viewer);
        });
    }

    @Override
    public Inventory inventory() {
        return inventory(player());
    }

    @Override
    public synchronized Inventory inventory(Player player) {
        if (player == null) return null;
        BukkitViewerSession session = sessions.get(player.getUniqueId());
        return session == null ? null : session.inventory();
    }

    @Override
    public DefaultBukkitGuiRuntime runtime() {
        return runtime;
    }

    @Override
    public void refresh() {
        editExecutor.submit(editor -> editor.refresh(RefreshPolicy.LAYOUT));
    }

    void refreshInventory() {
        scheduler.execute(this::refreshAllInventories);
    }

    private synchronized void refreshAllInventories() {
        final GuiSnapshot current = model.snapshot(true);
        for (Map.Entry<UUID, BukkitViewerSession> entry :
                new ArrayList<Map.Entry<UUID, BukkitViewerSession>>(sessions.entrySet())) {
            final UUID id = entry.getKey();
            final BukkitViewerSession expectedSession = entry.getValue();
            final GuiSnapshot previous = expectedSession.appliedSnapshot();
            runtime.computePlan(previous, current).whenComplete((plan, error) -> {
                if (error != null || plan == null) return;
                scheduler.execute(() -> {
                    synchronized (DefaultBukkitGui.this) {
                        BukkitViewerSession session = sessions.get(id);
                        if (session != expectedSession || !session.renderGate().accept(plan)) return;
                        applyPlan(session, current, plan);
                    }
                });
            });
        }
    }

    @Override
    public BukkitButton buttonAt(int slot) {
        return (BukkitButton) snapshot().layout().buttons().get(Slot.of(slot));
    }

    @Override
    public BukkitButton buttonAt(final Slot slot) {
        return (BukkitButton) snapshot().layout().buttons().get(slot);
    }

    @Override
    public BukkitInteractionPolicy interactionPolicy() {
        return interactionPolicy;
    }

    @Override
    public void interactionPolicy(BukkitInteractionPolicy policy) {
        if (policy == null) throw new NullPointerException("policy");
        this.interactionPolicy = policy;
    }

    @Override
    public boolean cancelEmptyClicks() {
        return interactionPolicy.cancelEmptyClicks();
    }

    @Override
    public boolean cancelBottomClicks() {
        return interactionPolicy.cancelBottomClicks();
    }

    @Override
    public void openInventory() {
        Player viewer = player();
        if (viewer == null) throw new IllegalStateException("player is not set");
        open(viewer);
    }

    private void applyPlan(BukkitViewerSession session, GuiSnapshot previous, GuiSnapshot current) {
        RenderPlan plan = planGenerator.generate(previous, current);
        if (!session.renderGate().accept(plan)) return;
        applyPlan(session, current, plan);
    }

    private void applyPlan(BukkitViewerSession session, GuiSnapshot current, RenderPlan plan) {
        if (session == null || plan == null) return;
        Inventory target = session.inventory();
        if (plan.inventoryRebuildRequired()) {
            target = runtime.inventories().createChest(this, current.layout().rows(), title);
            session.inventory(target);
            session.player().openInventory(target);
        }
        inventoryApplier.apply(plan, target, session.player());
        session.appliedSnapshot(current);
    }

    @Override
    public boolean isClosed() {
        GuiLifecycleState state = lifecycle.state();
        return state == GuiLifecycleState.CLOSED || state == GuiLifecycleState.DISPOSED;
    }

    @Override
    public GuiLifecycleState lifecycleState() {
        return lifecycle.state();
    }

    @Override
    public boolean open() {
        synchronized (this) {
            if (lifecycle.state() == GuiLifecycleState.NEW || lifecycle.state() == GuiLifecycleState.CLOSED) {
                boolean opened = lifecycle.open();
                if (opened) {
                    editExecutor = new SerialGuiEditExecutor(scheduler::execute, model);
                    model.refresh(RefreshPolicy.LAYOUT);
                }
                return opened;
            }
            return false;
        }
    }

    @Override
    public boolean open(Viewer viewer) {
        if (!(viewer instanceof BukkitViewer)) throw new IllegalArgumentException("viewer must be BukkitViewer");
        open(((BukkitViewer) viewer).player());
        return true;
    }

    @Override
    public boolean closeGui() {
        synchronized (this) {
            if (lifecycle.state() == GuiLifecycleState.CLOSED || lifecycle.state() == GuiLifecycleState.DISPOSED)
                return false;
            for (BukkitViewerSession session : new ArrayList<BukkitViewerSession>(sessions.values())) {
                session.player().closeInventory();
            }
            sessions.clear();
            boolean closed = lifecycle.close();
            if (closed) {
                editExecutor.close();
                model.refresh(RefreshPolicy.VISIBLE_SLOTS);
            }
            return closed;
        }
    }

    @Override
    public boolean close(Viewer viewer) {
        if (!(viewer instanceof BukkitViewer)) throw new IllegalArgumentException("viewer must be BukkitViewer");
        close(((BukkitViewer) viewer).player());
        return true;
    }

    @Override
    public boolean dispose() {
        synchronized (this) {
            if (lifecycle.state() == GuiLifecycleState.DISPOSED) return false;
            for (BukkitViewerSession session : new ArrayList<BukkitViewerSession>(sessions.values())) {
                session.player().closeInventory();
            }
            sessions.clear();
            boolean disposed = lifecycle.dispose();
            if (disposed) {
                editExecutor.close();
                onDisposed.run();
            }
            return disposed;
        }
    }

    @Override
    public CompletionStage<EditResult> editGui(Consumer<GuiEditor> editor) {
        if (isClosed()) return CompletableFuture.completedFuture(EditResult.CLOSED);
        CompletionStage<EditResult> result = editExecutor.submit(editor);
        result.whenComplete((editResult, error) -> {
            if (error == null && editResult == EditResult.APPLIED) {
                refreshInventory();
            } else if (error != null) {
                runtime.plugin().getLogger().log(Level.WARNING, "Failed to apply edit", error);
            }
        });
        return result;
    }

    @Override
    public EditResult editGuiNow(Consumer<GuiEditor> editor) {
        if (isClosed()) return EditResult.CLOSED;
        if (editor == null) throw new NullPointerException("editor");
        EditResult result = editExecutor.executeNow(editor);
        if (result == EditResult.APPLIED) refreshInventory();
        return result;
    }

    @Override
    public GuiSnapshot snapshot() {
        return model.snapshot(lifecycle.state() == GuiLifecycleState.OPEN);
    }

    @Override
    public BukkitGui copy() {
        BukkitGui copy = runtime.createGui();
        copy.title(title);
        copy.interactionPolicy(interactionPolicy);
        GuiSnapshot state = snapshot();
        copy.editGuiNow(editor -> {
            editor.layout(state.layout());
            for (Map.Entry<String, Object> entry : state.data().entrySet()) {
                editor.data(entry.getKey(), entry.getValue());
            }
        });
        return copy;
    }

    @Override
    public Collection<? extends Viewer> viewers() {
        synchronized (this) {
            Collection<BukkitViewer> result = new ArrayList<BukkitViewer>();
            for (BukkitViewerSession session : sessions.values()) result.add(session.viewer());
            return Collections.unmodifiableCollection(result);
        }
    }

    @Override
    public void close() {
        dispose();
    }
}
