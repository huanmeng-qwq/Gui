package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.ClickResult;
import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;
import me.huanmeng.gui.bukkit.api.BukkitButton;
import me.huanmeng.gui.bukkit.api.BukkitClickContext;
import me.huanmeng.gui.bukkit.api.BukkitGui;
import me.huanmeng.gui.bukkit.api.BukkitInteractionPolicy;
import me.huanmeng.gui.bukkit.api.BukkitViewer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

final class BukkitGuiEventListener implements Listener {
    private final DefaultBukkitGuiRuntime runtime;

    BukkitGuiEventListener(DefaultBukkitGuiRuntime runtime) {
        this.runtime = runtime;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        BukkitGui gui = guiFor(event.getView().getTopInventory());
        if (gui == null || runtime.isClosed()) return;
        BukkitInteractionPolicy policy = gui.interactionPolicy();
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            if (policy.cancelBottomClicks() || (policy.cancelMoveToGui() && event.isShiftClick())) {
                event.setCancelled(true);
            }
            return;
        }
        String clickName = event.getClick().name();
        if ((event.getHotbarButton() >= 0 && policy.cancelHotbarSwap())
                || ("SWAP_OFFHAND".equals(clickName) && policy.cancelOffhandSwap())
                || ("COLLECT_TO_CURSOR".equals(event.getAction().name()) && policy.cancelMoveToGui())) {
            event.setCancelled(true);
            return;
        }
        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;
        Button button = gui.buttonAt(event.getRawSlot());
        if (button == null) {
            if (gui.cancelEmptyClicks()) event.setCancelled(true);
            return;
        }
        ClickResult result = button.click(new ClickContext(gui, event));
        if (result.isCancelEvent()) {
            event.setCancelled(true);
        }
        if (result == ClickResult.REFRESH && gui instanceof DefaultBukkitGui) {
            ((DefaultBukkitGui) gui).refreshInventory();
        }
        if (result == ClickResult.CLOSE) event.getWhoClicked().closeInventory();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        BukkitGui gui = guiFor(event.getView().getTopInventory());
        if (gui == null || runtime.isClosed()) return;
        for (Integer rawSlot : event.getRawSlots()) {
            if (rawSlot >= 0 && rawSlot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (Gui registered : runtime.registry().all()) {
            if (registered instanceof DefaultBukkitGui) {
                DefaultBukkitGui gui = (DefaultBukkitGui) registered;
                Inventory inventory = gui.inventory(event.getPlayer());
                if (inventory != null) gui.handleInventoryClosed(event.getPlayer(), inventory);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        BukkitGui gui = guiFor(event.getInventory());
        if (gui != null && event.getPlayer() instanceof Player && gui instanceof DefaultBukkitGui) {
            Player player = (Player) event.getPlayer();
            ((DefaultBukkitGui) gui).handleInventoryClosed(player, event.getInventory());
        }
    }

    private BukkitGui guiFor(Inventory inventory) {
        if (inventory == null || !(inventory.getHolder() instanceof BukkitGui)) return null;
        BukkitGui gui = (BukkitGui) inventory.getHolder();
        return gui.runtime() == runtime ? gui : null;
    }

    private static final class ClickContext implements BukkitClickContext {
        private final BukkitGui gui;
        private final InventoryClickEvent event;

        private ClickContext(BukkitGui gui, InventoryClickEvent event) {
            this.gui = gui;
            this.event = event;
        }

        @Override
        public BukkitGui gui() {
            return gui;
        }

        @Override
        public Player player() {
            return (Player) event.getWhoClicked();
        }

        @Override
        public BukkitViewer viewer() {
            return new BukkitViewer(player());
        }

        @Override
        public Slot slot() {
            return Slot.of(event.getRawSlot());
        }

        @Override
        public BukkitButton button() {
            return gui.buttonAt(rawSlot());
        }

        @Override
        public int rawSlot() {
            return event.getRawSlot();
        }

        @Override
        public InventoryClickEvent event() {
            return event;
        }
    }
}
