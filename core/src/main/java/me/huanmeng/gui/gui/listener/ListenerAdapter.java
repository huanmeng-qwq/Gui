package me.huanmeng.gui.gui.listener;

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
public interface ListenerAdapter {
    /**
     * 当点击{@link org.bukkit.inventory.Inventory}时触发
     */
    void onInventoryClick(InventoryClickEvent e);

    /**
     * 当在{@link org.bukkit.inventory.Inventory}拖拽时触发
     */
    void onInventoryDrag(InventoryDragEvent e);

    /**
     * 当打开{@link org.bukkit.inventory.Inventory}时触发
     */
    void onInventoryOpen(InventoryOpenEvent e);

    /**
     * 当关闭{@link org.bukkit.inventory.Inventory}时触发
     */
    void onInventoryClose(InventoryCloseEvent e);

    /**
     * 当关闭{@link org.bukkit.plugin.Plugin}时触发
     */
    void onPluginDisabled(PluginDisableEvent e);
}
