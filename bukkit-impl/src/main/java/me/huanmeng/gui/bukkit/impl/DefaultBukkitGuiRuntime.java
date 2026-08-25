package me.huanmeng.gui.bukkit.impl;

import java.util.Objects;
import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.GuiRegistry;
import me.huanmeng.gui.api.GuiScheduler;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import me.huanmeng.gui.bukkit.api.BukkitGuiRuntime;
import org.bukkit.plugin.java.JavaPlugin;

public final class DefaultBukkitGuiRuntime implements BukkitGuiRuntime {
    private final JavaPlugin plugin;
    private final RuntimeSettings settings;
    private final DefaultGuiRegistry registry = new DefaultGuiRegistry();
    private final GuiScheduler scheduler;
    private volatile boolean closed;

    private DefaultBukkitGuiRuntime(JavaPlugin plugin, RuntimeSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.scheduler = new GuiScheduler() {
            @Override public void execute(Runnable task) {
                plugin.getServer().getScheduler().runTask(plugin, task);
            }
            @Override public void executeLater(Runnable task) { execute(task); }
        };
    }

    public static BukkitGuiRuntime create(JavaPlugin plugin, RuntimeSettings settings) {
        return new DefaultBukkitGuiRuntime(Objects.requireNonNull(plugin, "plugin"),
                Objects.requireNonNull(settings, "settings"));
    }

    @Override public JavaPlugin plugin() { return plugin; }
    @Override public RuntimeSettings settings() { return settings; }
    @Override public GuiRegistry registry() { return registry; }
    @Override public GuiScheduler scheduler() { return scheduler; }
    @Override public Gui createGui() {
        if (closed) throw new IllegalStateException("runtime is closed");
        Gui gui = new DefaultBukkitGui(scheduler);
        registry.register(gui);
        return gui;
    }
    @Override public boolean isClosed() { return closed; }
    @Override public void close() {
        if (!closed) {
            closed = true;
            registry.closeAll();
        }
    }
}
