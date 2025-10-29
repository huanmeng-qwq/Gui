package me.huanmeng.gui.gui.listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * Adapter interface for inventory event listeners.
 * <p>
 * This interface defines the contract for handling Bukkit inventory events in the GUI library.
 * It provides an abstraction layer between the GUI system and Bukkit's event system, allowing
 * for different implementations for different server platforms (Bukkit, Paper, etc.).
 *
 * <p>
 * The library provides two implementations:
 * <ul>
 *   <li>{@link BukkitEventListener} - For standard Bukkit/Spigot servers</li>
 *   <li>{@link PaperEventListener} - For Paper servers with enhanced API support</li>
 * </ul>
 * The appropriate implementation is automatically selected at runtime based on available classes.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/16
 * @see BukkitEventListener
 * @see PaperEventListener
 */
public interface ListenerAdapter {
    /**
     * Called when a player clicks in an inventory.
     * <p>
     * This method handles all types of inventory click events, including regular clicks,
     * shift-clicks, number key presses, and drag operations that complete with a click.
     * </p>
     *
     * @param e the inventory click event, never null
     */
    void onInventoryClick(InventoryClickEvent e);

    /**
     * Called when a player drags items across inventory slots.
     * <p>
     * This method handles drag operations where a player holds an item stack and drags
     * it across multiple slots to distribute items (e.g., left-drag to place one item per slot,
     * right-drag to place items evenly).
     * </p>
     *
     * @param e the inventory drag event, never null
     */
    void onInventoryDrag(InventoryDragEvent e);

    /**
     * Called when a player opens an inventory.
     * <p>
     * This method is invoked when any inventory is opened by a player, including both
     * GUI inventories managed by this library and vanilla Minecraft inventories.
     * </p>
     *
     * @param e the inventory open event, never null
     */
    void onInventoryOpen(InventoryOpenEvent e);

    /**
     * Called when a player closes an inventory.
     * <p>
     * This method handles inventory close events, allowing the GUI system to perform
     * cleanup operations, save state, or trigger navigation logic (e.g., opening a parent GUI).
     * </p>
     *
     * @param e the inventory close event, never null
     */
    void onInventoryClose(InventoryCloseEvent e);

    /**
     * Called when a plugin is disabled on the server.
     * <p>
     * This method allows the GUI system to perform cleanup operations when the plugin
     * that registered this listener is being disabled, such as closing all open GUIs
     * or unregistering event handlers.
     * </p>
     *
     * @param e the plugin disable event, never null
     */
    void onPluginDisabled(PluginDisableEvent e);
}
