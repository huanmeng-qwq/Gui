package me.huanmeng.opensource.bukkit.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * 2023/6/16<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class BukkitEventListener implements Listener {
    private final ListenerAdapter listenerAdapter;

    public BukkitEventListener(ListenerAdapter listenerAdapter) {
        this.listenerAdapter = listenerAdapter;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        listenerAdapter.onInventoryClick(e);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        listenerAdapter.onInventoryDrag(e);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        listenerAdapter.onInventoryOpen(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        listenerAdapter.onInventoryClose(e);
    }

    @EventHandler
    public void onPluginDisabled(PluginDisableEvent e) {
        listenerAdapter.onPluginDisabled(e);
    }
}
