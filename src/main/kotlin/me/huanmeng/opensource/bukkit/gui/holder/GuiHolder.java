package me.huanmeng.opensource.bukkit.gui.holder;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 2022/6/16<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class GuiHolder<G extends AbstractGui<G>> implements InventoryHolder {
    private final Player player;
    private Inventory inventory;
    private final G gui;

    public GuiHolder(Player player, G gui) {
        this.player = player;
        this.gui = gui;
    }

    public Player player() {
        return player;
    }

    public Inventory inventory() {
        return inventory;
    }

    public G gui() {
        return gui;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
