package me.huanmeng.opensource.bukkit.gui;

import com.google.common.base.Preconditions;
import me.huanmeng.opensource.bukkit.gui.event.InventorySwitchEvent;
import me.huanmeng.opensource.bukkit.gui.holder.GuiHolder;
import me.huanmeng.opensource.bukkit.scheduler.SchedulerAsync;
import me.huanmeng.opensource.bukkit.scheduler.SchedulerSync;
import me.huanmeng.opensource.scheduler.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class GuiManager implements Listener {
    private final JavaPlugin plugin;

    private static GuiManager instance;

    public static GuiManager instance() {
        return instance;
    }

    public GuiManager(JavaPlugin plugin) {
        Preconditions.checkArgument(instance == null, "instance is not null");
        Preconditions.checkNotNull(plugin, "plugin is null");
        GuiManager.instance = this;
        this.plugin = plugin;
        Schedulers.setSync(new SchedulerSync());
        Schedulers.setAsync(new SchedulerAsync());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final Map<UUID, AbstractGui<?>> userOpenGui = new ConcurrentHashMap<>();
    final Map<UUID, AbstractGui<?>> userNextOpenGui = new ConcurrentHashMap<>();

    public void setUserOpenGui(UUID uuid, AbstractGui<?> gui) {
        userOpenGui.put(uuid, gui);
    }

    public void removeUserOpenGui(UUID uuid) {
        userOpenGui.remove(uuid);
    }

    public AbstractGui<?> getUserOpenGui(UUID uuid) {
        return userOpenGui.get(uuid);
    }

    public boolean check(UUID uuid, Inventory inventory) {
        Preconditions.checkNotNull(uuid, "uuid is null");
        Preconditions.checkNotNull(inventory, "inventory is null");
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof GuiHolder)) {
            return false;
        }
        AbstractGui<?> gui = userOpenGui.get(uuid);
        if (gui == null) {
            return false;
        }
        Inventory inv = gui.getInventory();
        holder = inv.getHolder();
        Preconditions.checkNotNull(holder, "holder is null");
        return Objects.equals(holder.getInventory(), inventory);
    }

    public boolean isOpenGui(UUID user) {
        return userOpenGui.containsKey(user);
    }

    public boolean isOpenGui(UUID user, AbstractGui<?> gui) {
        return Objects.equals(getUserOpenGui(user), gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        onInventoryClick(e, player);
        if (e instanceof InventorySwitchEvent) {
            return;
        }
        // 玩家在自己背包里面改尝试切换物品栏(0-9)的物品 或切换物品至副手
        boolean swapOffhand = e.getClick().name().equals("SWAP_OFFHAND") || e.getClick().name().equals("HOTBAR_MOVE_AND_READD");
        if ((e.getClick() == ClickType.NUMBER_KEY || swapOffhand) && (Objects.equals(e.getClickedInventory(), e.getWhoClicked().getInventory())
                || e.getClickedInventory() instanceof CraftingInventory)
        ) {
            ClickType switchClickType = ClickType.UNKNOWN;
            if (swapOffhand) {
                switchClickType = e.getClick();
            }
            int slot = e.getHotbarButton();
            if (swapOffhand) {
                slot = e.getRawSlot();
            }
            InventorySwitchEvent event = new InventorySwitchEvent(e.getView(), InventoryType.SlotType.OUTSIDE, slot, switchClickType, e.getAction(), e.getHotbarButton());
            if (e.getClick() == ClickType.NUMBER_KEY) {
                event.setCurrentItem(player.getInventory().getItem(slot));
            } else if (swapOffhand) {
                event.setCurrentItem(e.getCurrentItem());
            }
            onInventoryClick(event, player);
            if (event.disable()) {
                return;
            }
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
        if (swapOffhand) {
            player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
        }
    }

    protected void onInventoryClick(InventoryClickEvent e, Player player) {
        UUID uuid = player.getUniqueId();
        if (!isOpenGui(uuid)) {
            return;
        }
        try {
            getUserOpenGui(uuid).onClick(e);
            e.setCancelled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            player.closeInventory();
            player.sendMessage("§c在处理您的点击请求时发生了错误！");
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        //noinspection ConstantValue
        if (e.getInventory() == null) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (!isOpenGui(uuid)) {
            return;
        }
        getUserOpenGui(uuid).onDarg(e);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        //noinspection ConstantValue
        if (null == e.getInventory()) {
            return;
        }
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (userNextOpenGui.containsKey(uuid)) {
            AbstractGui<?> gui = userNextOpenGui.remove(uuid);
            gui.onOpen();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        //noinspection ConstantValue
        if (e.getInventory() == null) {
            return;
        }
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        AbstractGui<?> gui = getUserOpenGui(uuid);
        if (gui != null) {
            gui.onClose();
        }
    }

    public void refreshGui(Player player) {
        UUID uuid = player.getUniqueId();
        if (isOpenGui(uuid)) {
            // 当更换gui后应该刷新所有按钮的显示。
            Runnable refreshRunnable = () -> getUserOpenGui(uuid).refresh(true);
            if (Bukkit.isPrimaryThread()) {
                refreshRunnable.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, refreshRunnable);
            }
        }
    }

    public JavaPlugin plugin() {
        return plugin;
    }
}
