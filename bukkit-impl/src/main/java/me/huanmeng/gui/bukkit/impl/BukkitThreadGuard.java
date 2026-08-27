package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.thread.RuntimeSettings;
import me.huanmeng.gui.api.thread.ThreadGuard;
import me.huanmeng.gui.api.thread.WrongThreadException;
import me.huanmeng.gui.api.thread.WrongThreadPolicy;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

final class BukkitThreadGuard implements ThreadGuard {
    private final JavaPlugin plugin;
    private final Server server;
    private final RuntimeSettings settings;
    private volatile boolean renderThreadDisabled;

    BukkitThreadGuard(JavaPlugin plugin, RuntimeSettings settings) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.settings = settings;
    }
    @Override public boolean isRenderThread() { return server.isPrimaryThread(); }
    @Override public void checkRenderThread() {
        if (server.isPrimaryThread()) return;
        WrongThreadPolicy policy = settings.wrongThreadPolicy();
        if (policy == WrongThreadPolicy.DISABLE_RENDER_THREAD) {
            if (!renderThreadDisabled) {
                renderThreadDisabled = true;
                plugin.getLogger().warning("Disabled async render-plan execution after wrong-thread access");
            }
            throw new WrongThreadException("Render-plan execution was disabled; Bukkit access still requires the primary thread");
        }
        String message = "Bukkit GUI inventory access must run on the primary server thread";
        if (policy == WrongThreadPolicy.WARN) {
            plugin.getLogger().warning(message + "; thread=" + Thread.currentThread().getName());
        }
        throw new WrongThreadException(message);
    }
}
