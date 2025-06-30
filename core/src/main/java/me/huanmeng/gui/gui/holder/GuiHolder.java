package me.huanmeng.gui.gui.holder;

import me.huanmeng.gui.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class GuiHolder<@NonNull G extends AbstractGui<G>> implements InventoryHolder {
    @NonNull
    private final Player player;
    private Inventory inventory;
    @NonNull
    private final G gui;

    public GuiHolder(@NonNull Player player, @NonNull G gui) {
        this.player = player;
        this.gui = gui;
    }

    @NonNull
    public Player player() {
        return player;
    }

    @NonNull
    public Inventory inventory() {
        return inventory;
    }

    @NonNull
    public G gui() {
        return gui;
    }

    public void setInventory(@NonNull Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inventory;
    }
}
