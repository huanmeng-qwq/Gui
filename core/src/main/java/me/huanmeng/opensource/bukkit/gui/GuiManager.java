package me.huanmeng.opensource.bukkit.gui;

import com.google.common.base.Preconditions;
import me.huanmeng.opensource.bukkit.Metrics;
import me.huanmeng.opensource.bukkit.component.ComponentConvert;
import me.huanmeng.opensource.bukkit.gui.event.InventorySwitchEvent;
import me.huanmeng.opensource.bukkit.gui.holder.GuiHolder;
import me.huanmeng.opensource.bukkit.gui.interfaces.GuiHandler;
import me.huanmeng.opensource.bukkit.gui.listener.BukkitEventListener;
import me.huanmeng.opensource.bukkit.gui.listener.ListenerAdapter;
import me.huanmeng.opensource.bukkit.scheduler.SchedulerAsync;
import me.huanmeng.opensource.bukkit.scheduler.SchedulerSync;
import me.huanmeng.opensource.scheduler.Schedulers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
public class GuiManager implements ListenerAdapter {
    @NonNull
    private final JavaPlugin plugin;

    private static GuiManager instance;
    @NonNull
    private final BukkitAudiences audiences;
    @NonNull
    private ComponentConvert componentConvert = ComponentConvert.getDefault();
    @NonNull
    private GuiHandler guiHandler = new GuiHandler.GuiHandlerDefaultImpl();

    @Nullable
    private Metrics metrics;

    private boolean processingClickEvent = false;

    @NonNull
    public static GuiManager instance() {
        return instance;
    }


    public GuiManager(@NonNull JavaPlugin plugin) {
        this(plugin, true);
    }

    public GuiManager(@NonNull JavaPlugin plugin, boolean registerListener) {
        Preconditions.checkNotNull(plugin, "plugin is null");
        if (GuiManager.instance == null) {// 避免构建多个GuiManager
            GuiManager.instance = this;
            Schedulers.setSync(new SchedulerSync());
            Schedulers.setAsync(new SchedulerAsync());
        }
        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);
        if (!Boolean.getBoolean("gui.disable-bStats")) {
            metrics = new Metrics(plugin, 18670, "2.2.3");
        }
        if (registerListener) {
            Bukkit.getPluginManager().registerEvents(new BukkitEventListener(this), plugin);
        }
    }

    @NonNull
    private final Map<UUID, AbstractGui<?>> userOpenGui = new ConcurrentHashMap<>();
    @NonNull
    final Map<UUID, AbstractGui<?>> userNextOpenGui = new ConcurrentHashMap<>();

    public void setUserOpenGui(@NonNull UUID uuid, @NonNull AbstractGui<?> gui) {
        userOpenGui.put(uuid, gui);
    }

    public void removeUserOpenGui(@NonNull UUID uuid) {
        userOpenGui.remove(uuid);
    }

    @Nullable
    public AbstractGui<?> getUserOpenGui(@NonNull UUID uuid) {
        return userOpenGui.get(uuid);
    }

    public boolean check(@NonNull UUID uuid, @NonNull Inventory inventory) {
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
        @NonNull Inventory inv = gui.getInventory();
        holder = inv.getHolder();
        Preconditions.checkNotNull(holder, "holder is null");
        return Objects.equals(holder.getInventory(), inventory);
    }

    public boolean isOpenGui(@NonNull UUID user) {
        return userOpenGui.containsKey(user);
    }

    public boolean isOpenGui(@NonNull UUID user, @NonNull AbstractGui<?> gui) {
        return Objects.equals(getUserOpenGui(user), gui);
    }

    public void onInventoryClick(InventoryClickEvent e) {
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
            if (e.getSlot() == e.getHotbarButton() && e.getHotbarButton() >= 0) {
                return;
            }
            ClickType switchClickType = ClickType.UNKNOWN;
            int slot = e.getHotbarButton();
            if (swapOffhand) {
                switchClickType = e.getClick();
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

    protected void onInventoryClick(@NonNull InventoryClickEvent e, @NonNull Player player) {
        processingClickEvent = true;
        UUID uuid = player.getUniqueId();
        if (!isOpenGui(uuid)) {
            return;
        }
        try {
            AbstractGui<?> gui = getUserOpenGui(uuid);
            if (gui != null) {
                gui.onClick(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            player.closeInventory();
            player.sendMessage("§c在处理您的点击请求时发生了错误！");
        }
        processingClickEvent = false;
    }

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
        AbstractGui<?> gui = getUserOpenGui(uuid);
        if (gui != null) {
            gui.onDarg(e);
        }
    }

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

    public void onInventoryClose(InventoryCloseEvent e) {
        //noinspection ConstantValue
        if (e.getInventory() == null) {
            return;
        }
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        if (!(e.getInventory().getHolder() instanceof GuiHolder)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        AbstractGui<?> gui = getUserOpenGui(uuid);
        if (gui != null) {
            gui.onClose();
        }
    }

    public void onPluginDisabled(PluginDisableEvent e) {
        if (e.getPlugin() == plugin) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                if (isOpenGui(uuid)) {
                    AbstractGui<?> gui = getUserOpenGui(uuid);
                    if (gui != null) {
                        gui.close(false, true);
                    }
                }
            }
            if (metrics != null) {
                metrics.shutdown();
            }
        }
    }

    public void refreshGui(@NonNull Player player) {
        UUID uuid = player.getUniqueId();
        if (isOpenGui(uuid)) {
            // 当更换gui后应该刷新所有按钮的显示。
            Runnable refreshRunnable = () -> {
                AbstractGui<?> gui = getUserOpenGui(uuid);
                if (gui != null) {
                    gui.refresh(true);
                }
            };
            if (Bukkit.isPrimaryThread()) {
                refreshRunnable.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, refreshRunnable);
            }
        }
    }

    @NonNull
    public JavaPlugin plugin() {
        return plugin;
    }

    @NonNull
    public BukkitAudiences audiences() {
        return audiences;
    }

    /**
     * 发送消息
     *
     * @param receiver 接收者
     * @param messages 消息
     */
    public static void sendMessage(CommandSender receiver, Component... messages) {
        if (receiver instanceof Player) {
            for (Component message : messages) {
                instance().audiences().player((Player) receiver).sendMessage(message);
            }
        } else if (receiver instanceof ConsoleCommandSender) {
            for (Component message : messages) {
                instance().audiences().console().sendMessage(message);
            }
        }
    }

    /**
     * @param componentConvert 转换器
     */
    public void setComponentConvert(@NonNull ComponentConvert componentConvert) {
        this.componentConvert = componentConvert;
    }

    @NonNull
    public ComponentConvert componentConvert() {
        return componentConvert;
    }

    @NonNull
    public GuiHandler guiHandler() {
        return guiHandler;
    }

    public boolean processingClickEvent() {
        return processingClickEvent;
    }

    public void setGuiHandler(@NonNull GuiHandler guiHandler) {
        this.guiHandler = guiHandler;
    }
}
