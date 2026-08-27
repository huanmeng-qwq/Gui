package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.ViewerFactory;
import org.bukkit.entity.Player;

public interface BukkitViewerFactory extends ViewerFactory<Player> {
    @Override
    BukkitViewer create(Player player);
}
