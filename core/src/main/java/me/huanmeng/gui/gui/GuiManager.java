package me.huanmeng.gui.gui;

import com.google.common.base.Preconditions;
import me.huanmeng.gui.Metrics;
import me.huanmeng.gui.gui.event.InventorySwitchEvent;
import me.huanmeng.gui.gui.holder.GuiHolder;
import me.huanmeng.gui.gui.interfaces.GuiHandler;
import me.huanmeng.gui.gui.listener.BukkitEventListener;
import me.huanmeng.gui.gui.listener.ListenerAdapter;
import me.huanmeng.gui.gui.listener.PaperEventListener;
import me.huanmeng.gui.scheduler.SchedulerAsync;
import me.huanmeng.gui.scheduler.SchedulerSync;
import me.huanmeng.gui.scheduler.Schedulers;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Central manager for the GUI library that handles GUI lifecycle, event registration, and player interactions.
 *
 * <p>This singleton class is responsible for:
 * <ul>
 *   <li>Registering and managing Bukkit/Paper event listeners</li>
 *   <li>Tracking open GUIs for all players</li>
 *   <li>Managing Adventure audiences for modern component support</li>
 *   <li>Initializing scheduler implementations (sync/async)</li>
 *   <li>Handling bStats metrics (can be disabled with {@code -Dgui.disable-bStats=true})</li>
 * </ul>
 *
 * <p><b>Usage:</b> Must be instantiated in your plugin's {@code onEnable()} method
 * and closed in {@code onDisable()} method.
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * public class MyPlugin extends JavaPlugin {
 *     private GuiManager guiManager;
 *
 *     @Override
 *     public void onEnable() {
 *         guiManager = new GuiManager(this);
 *     }
 *
 *     @Override
 *     public void onDisable() {
 *         if (guiManager != null) {
 *             guiManager.close();
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
public class GuiManager implements ListenerAdapter {
    /**
     * The plugin instance that owns this GUI manager.
     */
    @NonNull
    private final JavaPlugin plugin;

    /**
     * Singleton instance of the GUI manager.
     */
    private static GuiManager instance;

    /**
     * Adventure audiences for sending modern text components to players.
     */
    @NonNull
    private final BukkitAudiences audiences;

    /**
     * Custom handler for GUI-specific events and behaviors.
     */
    @NonNull
    private GuiHandler guiHandler = new GuiHandler.GuiHandlerDefaultImpl();

    /**
     * bStats metrics collector, null if disabled via system property.
     */
    @Nullable
    private Metrics metrics;

    /**
     * Flag indicating whether a click event is currently being processed.
     * Used to prevent recursive event handling.
     */
    private boolean processingClickEvent = false;

    /**
     * The registered Bukkit/Paper event listener instance.
     */
    private Listener registeredListener;

    /**
     * Returns the singleton instance of the GUI manager.
     *
     * @return the GUI manager instance
     * @throws NullPointerException if no instance has been created yet
     */
    @NonNull
    public static GuiManager instance() {
        return instance;
    }

    /**
     * Creates a new GUI manager with automatic listener registration.
     *
     * @param plugin the plugin instance that owns this manager
     */
    public GuiManager(@NonNull JavaPlugin plugin) {
        this(plugin, true);
    }

    /**
     * Creates a new GUI manager with optional listener registration.
     *
     * <p>This constructor initializes:
     * <ul>
     *   <li>The singleton instance (if not already set)</li>
     *   <li>Synchronous and asynchronous schedulers</li>
     *   <li>Adventure audiences for component support</li>
     *   <li>bStats metrics (unless disabled via system property)</li>
     *   <li>Event listeners (Paper or Bukkit based on server version)</li>
     * </ul>
     *
     * @param plugin the plugin instance that owns this manager
     * @param registerListener whether to automatically register Bukkit event listeners
     */
    public GuiManager(@NonNull JavaPlugin plugin, boolean registerListener) {
        Preconditions.checkNotNull(plugin, "plugin is null");
        if (GuiManager.instance == null) { // Prevent creating multiple GuiManager instances
            GuiManager.instance = this;
            Schedulers.setSync(new SchedulerSync());
            Schedulers.setAsync(new SchedulerAsync());
        }
        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);
        if (!Boolean.getBoolean("gui.disable-bStats")) {
            metrics = new Metrics(plugin, 18670, "2.5.5");
        }
        if (registerListener) {
            try {
                Class.forName("org.bukkit.event.inventory.InventoryCloseEvent$Reason");
                Bukkit.getPluginManager().registerEvents(registeredListener = new PaperEventListener(this), plugin);
            } catch (ClassNotFoundException e) {
                Bukkit.getPluginManager().registerEvents(registeredListener = new BukkitEventListener(this), plugin);
            }
        }
    }

    /**
     * Closes the GUI manager and cleans up all resources.
     *
     * <p>This method:
     * <ul>
     *   <li>Unregisters all event listeners</li>
     *   <li>Closes Adventure audiences</li>
     *   <li>Shuts down bStats metrics</li>
     *   <li>Clears the singleton instance</li>
     * </ul>
     *
     * <p><b>Important:</b> Must be called in your plugin's {@code onDisable()} method.
     */
    public void close() {
        if (registeredListener != null) {
            HandlerList.unregisterAll(registeredListener);
            registeredListener = null;
        }
        audiences.close();
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        GuiManager.instance = null;
    }

    /**
     * Map of currently open GUIs indexed by player UUID.
     */
    @NonNull
    private final Map<UUID, AbstractGui<?>> userOpenGui = new ConcurrentHashMap<>();

    /**
     * Map of GUIs scheduled to be opened next, indexed by player UUID.
     * Used to track GUIs between close and open events.
     */
    @NonNull
    final Map<UUID, AbstractGui<?>> userNextOpenGui = new ConcurrentHashMap<>();

    /**
     * Associates a GUI with a player's UUID.
     *
     * @param uuid the player's unique identifier
     * @param gui the GUI to associate with the player
     */
    public void setUserOpenGui(@NonNull UUID uuid, @NonNull AbstractGui<?> gui) {
        userOpenGui.put(uuid, gui);
    }

    /**
     * Removes the GUI association for a player.
     *
     * @param uuid the player's unique identifier
     */
    public void removeUserOpenGui(@NonNull UUID uuid) {
        userOpenGui.remove(uuid);
    }

    /**
     * Gets the currently open GUI for a player.
     *
     * @param uuid the player's unique identifier
     * @return the player's open GUI, or null if no GUI is open
     */
    @Nullable
    public AbstractGui<?> getUserOpenGui(@NonNull UUID uuid) {
        return userOpenGui.get(uuid);
    }

    /**
     * Verifies that the given inventory matches the player's currently open GUI.
     *
     * @param uuid the player's unique identifier
     * @param inventory the inventory to check
     * @return true if the inventory belongs to the player's open GUI
     * @throws NullPointerException if uuid or inventory is null
     */
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

    /**
     * Checks if a player has any GUI open.
     *
     * @param user the player's unique identifier
     * @return true if the player has a GUI open
     */
    public boolean isOpenGui(@NonNull UUID user) {
        return userOpenGui.containsKey(user);
    }

    /**
     * Checks if a player has a specific GUI open.
     *
     * @param user the player's unique identifier
     * @param gui the GUI to check for
     * @return true if the specified GUI is currently open for the player
     */
    public boolean isOpenGui(@NonNull UUID user, @NonNull AbstractGui<?> gui) {
        return Objects.equals(getUserOpenGui(user), gui);
    }

    /**
     * Handles inventory click events for all registered GUIs.
     *
     * <p>This method processes clicks in GUI inventories and handles special cases like:
     * <ul>
     *   <li>Number key presses (0-9) for hotbar swapping</li>
     *   <li>Offhand swap clicks (F key)</li>
     *   <li>Hotbar move and readd operations</li>
     * </ul>
     *
     * @param e the inventory click event
     */
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        onInventoryClick(e, player);
        if (e instanceof InventorySwitchEvent) {
            return;
        }
        // Player is trying to swap items in their own inventory (0-9 hotbar items) or swap to offhand
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

    /**
     * Processes an inventory click event for a specific player.
     *
     * <p>This method delegates click handling to the player's currently open GUI.
     * If an error occurs during processing, the player's inventory is closed and
     * they are notified of the error.
     *
     * @param e the inventory click event
     * @param player the player who clicked
     */
    protected void onInventoryClick(@NonNull InventoryClickEvent e, @NonNull Player player) {
        processingClickEvent = true;
        UUID uuid = player.getUniqueId();
        if (!isOpenGui(uuid)) {
            processingClickEvent = false;
            return;
        }
        try {
            AbstractGui<?> gui = getUserOpenGui(uuid);
            if (gui != null) {
                gui.onClick(e);
            }
        } catch (Throwable ex) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while processing the click event", ex);
            player.closeInventory();
            player.sendMessage("§cAn error occurred while processing your click request!");
        } finally {
            processingClickEvent = false;
        }
    }

    /**
     * Handles inventory drag events for all registered GUIs.
     *
     * <p>Delegates drag event handling to the player's currently open GUI.
     *
     * @param e the inventory drag event
     */
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

    /**
     * Handles inventory open events for all registered GUIs.
     *
     * <p>This method:
     * <ul>
     *   <li>Closes any previously open GUI for the player</li>
     *   <li>Triggers the open callback for newly opened GUIs</li>
     * </ul>
     *
     * @param e the inventory open event
     */
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        AbstractGui<?> alreadyOpen = getUserOpenGui(uuid);
        if (alreadyOpen != null) {
            alreadyOpen.onClose();
        }
        //noinspection ConstantValue
        if (null == e.getInventory()) {
            return;
        }
        if (userNextOpenGui.containsKey(uuid)) {
            AbstractGui<?> gui = userNextOpenGui.remove(uuid);
            gui.onOpen();
        }
    }

    /**
     * Handles inventory close events for all registered GUIs.
     *
     * <p>Triggers the close callback for the player's currently open GUI.
     *
     * @param e the inventory close event
     */
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

    /**
     * Handles plugin disable events.
     *
     * <p>When this plugin is disabled, closes all open GUIs and shuts down metrics.
     *
     * @param e the plugin disable event
     */
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

    /**
     * Refreshes the GUI for a specific player.
     *
     * <p>This method updates all button displays in the player's currently open GUI.
     * If called from a non-main thread, the refresh is scheduled on the main thread.
     *
     * @param player the player whose GUI should be refreshed
     */
    public void refreshGui(@NonNull Player player) {
        UUID uuid = player.getUniqueId();
        if (isOpenGui(uuid)) {
            // After changing the GUI, all button displays should be refreshed.
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

    /**
     * Returns the plugin instance that owns this GUI manager.
     *
     * @return the plugin instance
     */
    @NonNull
    public JavaPlugin plugin() {
        return plugin;
    }

    /**
     * Returns the Adventure audiences instance for sending modern text components.
     *
     * @return the Adventure audiences
     */
    @NonNull
    public BukkitAudiences audiences() {
        return audiences;
    }

    /**
     * Sends Adventure text component messages to a command sender.
     *
     * <p>Supports both players and console senders.
     *
     * @param receiver the message recipient
     * @param messages the Adventure components to send
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
     * Returns the custom GUI handler for managing GUI-specific behaviors.
     *
     * @return the GUI handler
     */
    @NonNull
    public GuiHandler guiHandler() {
        return guiHandler;
    }

    /**
     * Checks if a click event is currently being processed.
     *
     * <p>Used to prevent recursive event handling.
     *
     * @return true if a click event is being processed
     */
    public boolean processingClickEvent() {
        return processingClickEvent;
    }

    /**
     * Sets a custom GUI handler for managing GUI-specific behaviors.
     *
     * @param guiHandler the new GUI handler
     */
    public void setGuiHandler(@NonNull GuiHandler guiHandler) {
        this.guiHandler = guiHandler;
    }

}
