package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.GuiRuntime;
import me.huanmeng.gui.api.ViewerFactory;
import me.huanmeng.gui.api.thread.RuntimeSettings;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface BukkitGuiRuntime extends GuiRuntime {
    @Override
    BukkitGui createGui();

    @Override
    BukkitViewerFactory viewerFactory();

    JavaPlugin plugin();

    RuntimeSettings settings();

    BukkitCapabilities capabilities();

    BukkitItemAdapter items();

    BukkitInventoryAdapter inventories();
}
