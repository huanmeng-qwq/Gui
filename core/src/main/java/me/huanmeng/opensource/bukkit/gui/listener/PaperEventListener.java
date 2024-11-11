package me.huanmeng.opensource.bukkit.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * 2024/11/3<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class PaperEventListener implements Listener {
    private static Class REASON_CLASS;
    private static Object REASON_OPEN_NEW;
    private static MethodHandle GET_REASON_METHOD;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            REASON_CLASS = Class.forName("org.bukkit.event.inventory.InventoryCloseEvent$Reason");
            REASON_OPEN_NEW = Enum.valueOf(REASON_CLASS, "OPEN_NEW");

            GET_REASON_METHOD = lookup.unreflect(InventoryCloseEvent.class.getDeclaredMethod("getReason"));
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    private final ListenerAdapter listenerAdapter;

    public PaperEventListener(ListenerAdapter listenerAdapter) {
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
        try {
            Object reason = GET_REASON_METHOD.bindTo(e).invoke();
            if (reason == REASON_OPEN_NEW) {
                return;
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        listenerAdapter.onInventoryClose(e);
    }

    @EventHandler
    public void onPluginDisabled(PluginDisableEvent e) {
        listenerAdapter.onPluginDisabled(e);
    }
}
