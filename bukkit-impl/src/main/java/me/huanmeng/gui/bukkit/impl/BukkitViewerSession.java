package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.GuiSnapshot;
import me.huanmeng.gui.bukkit.api.BukkitViewer;
import me.huanmeng.gui.impl.render.RenderPlanGate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/** Mutable platform state owned by one viewer of one GUI. */
final class BukkitViewerSession {
    private final BukkitViewer viewer;
    private final RenderPlanGate renderGate = new RenderPlanGate();
    private Inventory inventory;
    private GuiSnapshot appliedSnapshot;

    BukkitViewerSession(BukkitViewer viewer, Inventory inventory) {
        if (viewer == null) throw new NullPointerException("viewer");
        if (inventory == null) throw new NullPointerException("inventory");
        this.viewer = viewer;
        this.inventory = inventory;
    }

    BukkitViewer viewer() {
        return viewer;
    }

    Player player() {
        return viewer.player();
    }

    Inventory inventory() {
        return inventory;
    }

    void inventory(Inventory inventory) {
        if (inventory == null) throw new NullPointerException("inventory");
        this.inventory = inventory;
    }

    GuiSnapshot appliedSnapshot() {
        return appliedSnapshot;
    }

    void appliedSnapshot(GuiSnapshot appliedSnapshot) {
        this.appliedSnapshot = appliedSnapshot;
    }

    RenderPlanGate renderGate() {
        return renderGate;
    }
}
