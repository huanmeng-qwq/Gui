package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.GuiRuntime;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import org.bukkit.plugin.java.JavaPlugin;

public interface BukkitGuiRuntime extends GuiRuntime {
    JavaPlugin plugin();
    RuntimeSettings settings();
}
