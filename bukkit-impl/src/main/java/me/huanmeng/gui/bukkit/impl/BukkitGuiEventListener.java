package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.ClickResult;
import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.layout.Button;
import me.huanmeng.gui.api.layout.Slot;
import me.huanmeng.gui.bukkit.api.BukkitButton;
import me.huanmeng.gui.bukkit.api.BukkitClickContext;
import me.huanmeng.gui.bukkit.api.BukkitGui;
import me.huanmeng.gui.api.InteractionPolicy;
import me.huanmeng.gui.bukkit.api.BukkitViewer;
import org.bukkit.Material;
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
        if (!(event.getWhoClicked() instanceof Player)) return;

        Inventory topInventory = event.getView().getTopInventory();
        BukkitGui gui = guiFor(topInventory);
        if (gui == null || runtime.isClosed()) return;

        try {
            InteractionPolicy policy = gui.interactionPolicy();
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory == null) {
                if (policy.cancelEmptyClicks()) event.setCancelled(true);
                return;
            }
            if (clickedInventory == event.getView().getBottomInventory()) {
                if (policy.cancelBottomClicks()
                        || (policy.cancelMoveToGui() && event.isShiftClick())) {
                    event.setCancelled(true);
                }
                return;
            }
            if (clickedInventory != topInventory) return;

            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= topInventory.getSize()) {
                event.setCancelled(true);
                return;
            }

            Button button = gui.buttonAt(rawSlot);
            if (button != null) {
                ClickResult result = button.click(new ClickContext(gui, event));
                if (result == null) result = ClickResult.CANCEL;
                if (result == ClickResult.ALLOW) {
                    event.setCancelled(false);
                } else if (result.isCancelEvent()) {
                    event.setCancelled(true);
                }
                if (result == ClickResult.REFRESH && gui instanceof DefaultBukkitGui) {
                    ((DefaultBukkitGui) gui).refreshInventory();
                } else if (result == ClickResult.CLOSE) {
                    event.getWhoClicked().closeInventory();
                }
            } else if (isAir(event.getCurrentItem())) {
                if (event.getHotbarButton() >= 0 && policy.cancelHotbarSwap()) {
                    event.setCancelled(true);
                }
                if (policy.cancelEmptyClicks()) event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }

            String clickName = event.getClick().name();
            boolean offhandSwap = "SWAP_OFFHAND".equals(clickName)
                    || "HOTBAR_MOVE_AND_READD".equals(clickName);
            boolean collectToCursor = "COLLECT_TO_CURSOR".equals(event.getAction().name());
            if ((offhandSwap && policy.cancelOffhandSwap())
                    || (collectToCursor && policy.cancelMoveToGui())
                    || (!isAir(event.getCursor()) && policy.cancelMoveToGui())) {
                event.setCancelled(true);
            }
        } catch (Throwable error) {
            runtime.plugin().getLogger().log(java.util.logging.Level.SEVERE,
                    "An error occurred while processing the click event", error);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("§cAn error occurred while processing your click request!");
        }
    }

    private static boolean isAir(org.bukkit.inventory.ItemStack item) {
        return item == null || item.getType() == Material.AIR;
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
