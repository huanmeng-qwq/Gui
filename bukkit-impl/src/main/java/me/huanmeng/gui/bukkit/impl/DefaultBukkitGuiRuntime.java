package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.GuiRegistry;
import me.huanmeng.gui.api.GuiScheduler;
import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.api.ViewerFactory;
import me.huanmeng.gui.api.render.RenderPlan;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import me.huanmeng.gui.api.thread.ThreadGuard;
import me.huanmeng.gui.bukkit.api.BukkitCapabilities;
import me.huanmeng.gui.bukkit.api.BukkitGui;
import me.huanmeng.gui.bukkit.api.BukkitGuiRuntime;
import me.huanmeng.gui.bukkit.api.BukkitInventoryAdapter;
import me.huanmeng.gui.bukkit.api.BukkitItemAdapter;
import me.huanmeng.gui.bukkit.api.BukkitViewer;
import me.huanmeng.gui.bukkit.api.BukkitViewerFactory;
import me.huanmeng.gui.impl.render.DefaultRenderPlanGenerator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class DefaultBukkitGuiRuntime implements BukkitGuiRuntime {
    private static final BukkitViewerFactory PLAYER_VIEWER_FACTORY = BukkitViewer::new;
    private final JavaPlugin plugin;
    private final RuntimeSettings settings;
    private final DefaultGuiRegistry registry = new DefaultGuiRegistry();
    private final GuiScheduler scheduler;
    private final BukkitCapabilities capabilities;
    private final BukkitThreadGuard threadGuard;
    private final BukkitItemAdapter itemAdapter;
    private final BukkitInventoryAdapter inventoryAdapter;
    private final BukkitGuiEventListener listener;
    private final ExecutorService renderPlanExecutor;
    private volatile boolean closed;

    private DefaultBukkitGuiRuntime(JavaPlugin plugin, RuntimeSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.scheduler = new GuiScheduler() {
            @Override
            public void execute(Runnable task) {
                if (plugin.getServer().isPrimaryThread()) task.run();
                else plugin.getServer().getScheduler().runTask(plugin, task);
            }

            @Override
            public void executeLater(Runnable task) {
                plugin.getServer().getScheduler().runTask(plugin, task);
            }
        };
        this.capabilities = new DefaultBukkitCapabilities(plugin.getServer());
        this.threadGuard = new BukkitThreadGuard(plugin, settings);
        this.itemAdapter = new DefaultBukkitItemAdapter();
        this.inventoryAdapter = new DefaultBukkitInventoryAdapter(threadGuard);
        this.listener = new BukkitGuiEventListener(this);
        this.renderPlanExecutor = settings.renderThreadEnabled()
                ? Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "gui-render-plan-" + plugin.getName());
            thread.setDaemon(true);
            return thread;
        }) : null;
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static BukkitGuiRuntime create(JavaPlugin plugin, RuntimeSettings settings) {
        return new DefaultBukkitGuiRuntime(Objects.requireNonNull(plugin, "plugin"),
                Objects.requireNonNull(settings, "settings"));
    }

    @Override
    public JavaPlugin plugin() {
        return plugin;
    }

    @Override
    public RuntimeSettings settings() {
        return settings;
    }

    @Override
    public BukkitCapabilities capabilities() {
        return capabilities;
    }

    @Override
    public BukkitItemAdapter items() {
        return itemAdapter;
    }

    @Override
    public BukkitInventoryAdapter inventories() {
        return inventoryAdapter;
    }

    @Override
    public ThreadGuard threadGuard() {
        return threadGuard;
    }

    @Override
    public BukkitViewerFactory viewerFactory() {
        return PLAYER_VIEWER_FACTORY;
    }

    CompletionStage<RenderPlan> computePlan(GuiSnapshot previous, GuiSnapshot current) {
        if (closed) return CompletableFuture.completedFuture(null);
        if (renderPlanExecutor == null) {
            return CompletableFuture.completedFuture(new DefaultRenderPlanGenerator().generate(previous, current));
        }
        return CompletableFuture.supplyAsync(
                () -> new DefaultRenderPlanGenerator().generate(previous, current), renderPlanExecutor);
    }

    @Override
    public GuiRegistry registry() {
        return registry;
    }

    @Override
    public GuiScheduler scheduler() {
        return scheduler;
    }

    @Override
    public BukkitGui createGui() {
        if (closed) throw new IllegalStateException("runtime is closed");
        final BukkitGui[] holder = new BukkitGui[1];
        holder[0] = new DefaultBukkitGui(this, scheduler, () -> registry.unregister(holder[0]));
        registry.register(holder[0]);
        return holder[0];
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            HandlerList.unregisterAll(listener);
            registry.closeAll();
            if (renderPlanExecutor != null) renderPlanExecutor.shutdownNow();
        }
    }
}
