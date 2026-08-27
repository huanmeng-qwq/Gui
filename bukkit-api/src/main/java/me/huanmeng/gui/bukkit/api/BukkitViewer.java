package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.Viewer;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class BukkitViewer implements Viewer {
    private final Player player;

    public BukkitViewer(Player player) {
        if (player == null) throw new NullPointerException("player");
        this.player = player;
    }

    public Player player() {
        return player;
    }

    @Override
    public UUID uniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BukkitViewer && uniqueId().equals(((BukkitViewer) other).uniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
