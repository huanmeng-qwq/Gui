package me.huanmeng.gui.gui;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.draw.GuiDraw;
import me.huanmeng.gui.gui.enums.Result;
import me.huanmeng.gui.gui.holder.GuiHolder;
import me.huanmeng.gui.gui.interfaces.CustomResultHandler;
import me.huanmeng.gui.gui.interfaces.GuiBottomClick;
import me.huanmeng.gui.gui.interfaces.GuiClick;
import me.huanmeng.gui.gui.interfaces.GuiEmptyItemClick;
import me.huanmeng.gui.gui.interfaces.GuiTick;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import me.huanmeng.gui.scheduler.Scheduler;
import me.huanmeng.gui.scheduler.Schedulers;
import me.huanmeng.gui.tick.TickManager;
import me.huanmeng.gui.util.item.ItemUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all GUI implementations in the library.
 * <p>
 * This class provides the core functionality for creating and managing custom inventory GUIs in Bukkit/Paper.
 * It handles button management with priority levels, event handling (click, drag, close), inventory lifecycle,
 * and player inventory interaction controls.
 *
 * <p>
 * <b>Button Priority System:</b>
 * <ul>
 *   <li>{@link #editButtons} - Highest priority (internal modifications)</li>
 *   <li>{@link #attachedButtons} - Medium priority (dynamically attached buttons)</li>
 *   <li>{@link #buttons} - Default priority (standard buttons)</li>
 * </ul>
 *
 * <p>
 * <b>Event Cancellation Flags:</b>
 * The class provides fine-grained control over inventory interactions through various cancellation flags:
 * {@link #cancelClickOther}, {@link #cancelClickBottom}, {@link #cancelMoveHotBarItemToSelf},
 * {@link #cancelMoveItemToSelf}, {@link #cancelMoveItemToBottom}.
 *
 * @param <G> Self-bounded generic type for method chaining with the correct subclass type
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("ALL")
public abstract class AbstractGui<@NonNull G extends AbstractGui<@NonNull G>> implements GuiTick {
    /**
     * The player viewing this GUI
     */
    @Nullable
    protected Player player;

    /**
     * The title displayed in the inventory GUI
     */
    @NonNull
    protected Component title = Component.translatable("container.chest");

    /**
     * Default priority button set
     */
    @NonNull
    protected Set<GuiButton> buttons = new HashSet<>(10);

    /**
     * Higher priority buttons than the default set (dynamically attached)
     */
    @NonNull
    protected Set<GuiButton> attachedButtons = new HashSet<>(10);

    /**
     * Highest priority buttons for internal modifications
     */
    @NonNull
    protected Set<GuiButton> editButtons = new HashSet<>(10);

    /**
     * Cached inventory instance
     */
    @Nullable
    protected Inventory cacheInventory;

    /**
     * Function to retrieve the parent/back GUI for navigation
     */
    @Nullable
    protected Function<@NonNull Player, @NonNull AbstractGui<?>> backGuiGetter;

    /**
     * Runnable to execute when navigating back (alternative to backGuiGetter)
     */
    @Nullable
    protected Runnable backGuiRunner;

    /**
     * The size of the inventory in slots
     */
    private int size;

    /**
     * Whether to cancel click events on empty slots.
     * If true, clicking empty areas will be cancelled.
     */
    protected boolean cancelClickOther = true;

    /**
     * Whether to cancel clicks in the bottom {@link InventoryView} (player inventory) or offhand swaps.
     * If true, bottom inventory interactions are cancelled.
     */
    protected boolean cancelClickBottom = true;

    /**
     * Whether to cancel hotbar key presses that would move items from player inventory to this GUI.
     * If true, pressing number keys (1-9) to swap items is cancelled.
     */
    protected boolean cancelMoveHotBarItemToSelf = true;

    /**
     * Whether to cancel moving items from player inventory to this GUI via mouse or shift-click.
     * If true, prevents placing held items or shift-clicking items into the GUI.
     *
     * @see #cancelClickBottom
     */
    protected boolean cancelMoveItemToSelf = true;

    /**
     * Whether to cancel moving items from this GUI to the player's bottom inventory
     */
    protected boolean cancelMoveItemToBottom = true;

    /**
     * Whether player inventory slots can have interactive buttons
     */
    protected boolean enablePlayerInventory = false;

    /**
     * Flag indicating if a click event is currently being processed
     */
    protected boolean processingClickEvent;

    /**
     * Whether to disable all click event processing.
     * If true, all {@link InventoryClickEvent}s will be cancelled immediately without any handling.
     * Use with caution as this bypasses all button interactions.
     */
    protected boolean disableClick = false;

    /**
     * Whether this GUI can be reopened after closing
     */
    protected boolean allowReopen = true;

    /**
     * Whether the GUI is currently closed
     */
    boolean close = true;

    /**
     * Whether the GUI is in the process of closing
     */
    boolean closing = true;

    /**
     * List of tick callbacks to execute periodically
     */
    @NonNull
    protected List<Consumer<G>> tickles = new ArrayList<>();

    /**
     * Interval in ticks between periodic updates (default: 100 ticks = 5 seconds)
     */
    protected int intervalTick = 20 * 5;

    /**
     * Whether to automatically refresh the GUI on each tick
     */
    protected boolean tickRefresh = true;

    /**
     * Custom click handler for the GUI
     */
    @Nullable
    protected GuiClick guiClick;

    /**
     * Handler for clicks on empty slots
     */
    @Nullable
    protected GuiEmptyItemClick guiEmptyItemClick;

    /**
     * Handler for clicks in the bottom inventory (player inventory)
     */
    @Nullable
    protected GuiBottomClick guiBottomClick;

    /**
     * Callback to execute when the GUI is closed
     */
    @Nullable
    protected Consumer<@NonNull G> whenClose;

    /**
     * Custom result handler for processing button click results
     */
    @Nullable
    protected CustomResultHandler customResultHandler;

    /**
     * The scheduled tick task for periodic GUI updates
     */
    protected Scheduler.@Nullable Task tickTask;

    /**
     * Error message provider when inventory click processing fails.
     * Returns a localized error message based on the player's locale.
     */
    @NonNull
    protected Function<@NonNull HumanEntity, @Nullable Component> errorMessage =
            p -> Component.text("Unable to process your click request, please contact an administrator.", NamedTextColor.RED);

    /**
     * Metadata storage for custom data associated with this GUI
     */
    protected Map<String, Object> metadata = new HashMap<>(2);

    /**
     * The GuiManager instance managing this GUI
     */
    protected GuiManager manager = GuiManager.instance();

    /**
     * Sets the target player for this GUI.
     *
     * @param player The player who will view this GUI
     */
    public void setPlayer(@NonNull Player player) {
        this.player = player;
    }

    /**
     * Gets the target player viewing this GUI.
     *
     * @return The player viewing this GUI, or null if not set
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Initializes the GUI with a title and size.
     *
     * @param title    The title to display
     * @param itemSize The number of slots in the inventory
     * @deprecated Use {@link #init(Component, int)} for Adventure Component support
     */
    @Deprecated
    protected void init(@NonNull String title, int itemSize) {
        init(LegacyComponentSerializer.legacySection().deserialize(title), itemSize);
    }

    /**
     * Initializes the GUI with a title and size.
     *
     * @param title    The Adventure Component title to display
     * @param itemSize The number of slots in the inventory
     */
    protected void init(@NonNull Component title, int itemSize) {
        this.title = title;
        this.size = itemSize;
    }

    /**
     * Opens the GUI inventory for the player.
     * This method must be implemented by subclasses to handle the specific inventory type.
     *
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public abstract G openGui();

    /**
     * Creates an empty {@link Inventory} with the specified holder.
     * This method must be implemented by subclasses to create the appropriate inventory type.
     *
     * @param holder The inventory holder
     * @return The created inventory
     */
    @NonNull
    protected abstract Inventory build(@NonNull InventoryHolder holder);

    /**
     * Called when the GUI is opened.
     * Initializes the GUI state and starts the tick task if configured.
     * <p>
     * This is an internal method called by {@link GuiManager} during the inventory open event.
     * </p>
     */
    void onOpen() {
        close = false;
        closing = false;
        if (schedulerTick() > 0) {
            tickTask = TickManager.tick(this, scheduler(), schedulerTick());
        }
    }

    /**
     * Sets whether the GUI should automatically refresh on each tick.
     *
     * @param tickRefresh true to enable automatic refresh, false to disable
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G setTickRefresh(boolean tickRefresh) {
        this.tickRefresh = tickRefresh;
        return self();
    }

    /**
     * Called when the GUI is closed.
     * <p>
     * This method is invoked from {@link InventoryCloseEvent}, or when reopening due to a title update.
     * It stops the tick task and executes any registered close callbacks.
     * </p>
     */
    public void onClose() {
        close = true;
        if (tickTask != null) {
            tickTask.stop();
        }
        if (whenClose != null) {
            try {
                whenClose.accept(self());
            } catch (Exception e) {
                manager.plugin().getLogger().log(Level.SEVERE, "An error occurred while executing the whenClose action: ", e);
            }
        }
    }

    /**
     * Closes the GUI with options to open parent GUI and force close.
     *
     * @param openParent Whether to open the parent/back GUI after closing this one
     * @param force      Whether to force close even if the GUI is already closing or the parent doesn't allow reopening
     */
    public void close(boolean openParent, boolean force) {
        if (!force && (close || closing)) {
            return;
        }
        closing = true;
        if (processingClickEvent || !Bukkit.isPrimaryThread()) {
            // Delay the close method to the next tick when processing click events,
            // because by the next tick the current click event will have finished processing
            Schedulers.sync().runLater(() -> {
                closing = false;
                close(openParent, force);
            }, 1);
            return;
        }
        closing = false;
        if (openParent) {
            if (backGuiGetter != null) {
                AbstractGui<?> gui = backGuiGetter.apply(player);
                if (gui.isAllowReopenFrom(self()) || force) {
                    gui.setPlayer(player);
                    gui.openGui();
                    return;
                }
            } else if (backGuiRunner != null) {
                backGuiRunner.run();
            } else {
                player.closeInventory();
            }
        } else {
            player.closeInventory();
        }
    }

    /**
     * Closes this GUI without opening a parent GUI.
     * Equivalent to {@code close(false, false)}.
     */
    public void close() {
        close(false, false);
    }

    /**
     * Navigates back to the parent GUI.
     * If no parent GUI is configured, closes the current GUI.
     */
    public void back() {
        back(false);
    }

    /**
     * Navigates back to the parent GUI with optional force flag.
     * If no parent GUI is configured, closes the current GUI.
     *
     * @param force Whether to force navigation back even if the parent GUI doesn't allow reopening
     */
    public void back(boolean force) {
        close(true, force);
    }

    /**
     * Adds a tick callback to be executed periodically.
     *
     * @param tickle The callback to execute on each tick
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G addTick(@NonNull Consumer<G> tickle) {
        tickles.add(tickle);
        return self();
    }

    /**
     * Sets the tick interval for periodic updates.
     *
     * @param tick The interval in ticks (20 ticks = 1 second)
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G tick(int tick) {
        this.intervalTick = tick;
        return self();
    }

    /**
     * Sets a custom error message provider for when click processing fails.
     *
     * @param errorMessage Function that generates an error message for a given player
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G errorMessage(@NonNull Function<@NonNull HumanEntity, @Nullable Component> errorMessage) {
        this.errorMessage = errorMessage;
        return self();
    }

    /**
     * Executes all registered tick callbacks and optionally refreshes the GUI.
     * This method is called periodically by the {@link TickManager} based on the configured interval.
     */
    @Override
    public void run() {
        if (close) {
            return;
        }
        if (processingClickEvent) {
            processingClickEvent = false;
        }
        for (int i = 0; i < tickles.size(); i++) {
            tickles.get(i).accept(self());
        }
        if (tickRefresh) {
            refresh(false);
        }
    }

    @Override
    @NonNull
    public Scheduler scheduler() {
        return Schedulers.async();
    }

    @Override
    public int schedulerTick() {
        return intervalTick;
    }

    /**
     * Checks if this GUI allows being reopened from another GUI.
     *
     * @param gui The GUI requesting to navigate back to this GUI
     * @param <G> The type of the requesting GUI
     * @return true if reopening is allowed, false otherwise
     */
    @NonNull
    protected <G extends AbstractGui<G>> boolean isAllowReopenFrom(@NonNull G gui) {
        return allowReopen;
    }

    /**
     * Pre-caches this GUI as the next GUI to be opened for the player.
     * Used internally during the GUI open process.
     *
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    protected G precache() {
        manager.userNextOpenGui.put(player.getUniqueId(), this);
        return self();
    }

    /**
     * Caches the inventory and registers this GUI as currently open for the player.
     * Used internally after creating an inventory.
     *
     * @param inventory The inventory to cache
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    protected G cache(@NonNull Inventory inventory) {
        this.cacheInventory = inventory;
        ((GuiHolder) inventory.getHolder()).setInventory(inventory);
        manager.setUserOpenGui(player.getUniqueId(), this);
        return self();
    }


    /**
     * Refreshes and refills all items in the inventory.
     *
     * @param all Whether to clear the entire inventory before refilling.
     *            If true, clears all slots; if false, only updates existing button slots.
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G refresh(boolean all) {
        fillItems(cacheInventory, all);
        return self();
    }

    /**
     * Removes this GUI from the cache, unregistering it from the {@link GuiManager}.
     * Used internally during GUI cleanup.
     *
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    protected G unCache() {
        AbstractGui<?> gui = manager.userNextOpenGui.get(player.getUniqueId());
        if (gui == this) {
            manager.removeUserOpenGui(player.getUniqueId());
        }
        if (manager.getUserOpenGui(player.getUniqueId()) == this) {
            manager.removeUserOpenGui(player.getUniqueId());
        }
        return self();
    }

    /**
     * Creates a {@link GuiHolder} for this GUI.
     * The holder is used to identify GUI inventories and access the GUI instance from inventory events.
     *
     * @return A new GuiHolder instance
     */
    @NonNull
    protected final InventoryHolder createHolder() {
        return new GuiHolder<>(player, self());
    }

    /**
     * Gets the default priority button set to be filled in the inventory.
     *
     * @return The set of buttons to fill
     */
    @NonNull
    protected Set<GuiButton> getFillItems() {
        return buttons;
    }

    /**
     * Gets the set of dynamically attached buttons (medium priority).
     *
     * @return The set of attached buttons
     */
    @NonNull
    public Set<GuiButton> getAttachedItems() {
        return attachedButtons;
    }

    /**
     * Adds or updates an attached button in the GUI.
     * If a button already exists at the same slot, it will be replaced.
     *
     * @param button The button to attach
     * @return This GUI instance for method chaining
     */
    @NonNull
    public G addAttachedButton(@NonNull GuiButton button) {
        attachedButtons.remove(button);
        attachedButtons.add(button);
        return self();
    }

    /**
     * Sets a button at the specified slot with highest priority (edit priority).
     * This will override any existing buttons at this slot from lower priority sets.
     *
     * @param slot   The slot position
     * @param button The button to set, or null to clear the slot
     */
    protected void setButton(@NonNull Slot slot, @Nullable Button button) {
        GuiButton guiButton = new GuiButton(slot, button);
        buttons.remove(guiButton);
        attachedButtons.remove(guiButton);
        editButtons.add(guiButton);
    }

    /**
     * Gets the button at a specific inventory index with custom filtering.
     * Searches in priority order: editButtons → buttons → attachedButtons.
     *
     * @param index     The inventory slot index
     * @param predicate Filter predicate to apply when searching for buttons
     * @return The button at the index, or null if none found
     */
    @Nullable
    public GuiButton getButton(int index, Predicate<GuiButton> predicate) {
        return editButtons
                .stream()
                .filter(predicate)
                .filter(e -> e.getIndex() == index)
                .findFirst()
                .orElseGet(
                        () -> buttons
                                .stream()
                                .filter(predicate)
                                .filter(e -> e.getIndex() == index)
                                .findFirst()
                                .orElseGet(
                                        () -> attachedButtons
                                                .stream()
                                                .filter(predicate)
                                                .filter(e -> e.getIndex() == index)
                                                .findFirst()
                                                .orElse(null)
                                )
                );
    }

    /**
     * Gets the button at a specific inventory index.
     *
     * @param index The inventory slot index
     * @return The button at the index, or null if none found
     */
    public GuiButton getButton(int index) {
        return getButton(index, t -> true);
    }

    /**
     * Gets the button at a specific slot.
     *
     * @param slot The slot position
     * @return The button at the slot, or null if none found
     */
    public GuiButton getButton(Slot slot) {
        return getButton(slot.getIndex(), btn -> btn.isPlayerInventory() == slot.isPlayer());
    }

    /**
     * Gets the ItemStack at a specific inventory index.
     *
     * @param index The inventory slot index
     * @return The ItemStack at the index, or null if empty
     */
    public ItemStack getItem(int index) {
        return cacheInventory.getItem(index);
    }

    /**
     * Gets the ItemStack at a specific slot.
     *
     * @param slot The slot position
     * @return The ItemStack at the slot, or null if empty
     */
    public ItemStack getItem(Slot slot) {
        return getItem(slot.getIndex());
    }

    /**
     * Gets all ItemStacks from multiple slots.
     *
     * @param slots The slots to retrieve items from
     * @return An array of ItemStacks corresponding to the slots
     */
    public ItemStack[] getItems(Slots slots) {
        return Arrays.stream(slots.slots(self())).map(this::getItem).toArray(ItemStack[]::new);
    }

    /**
     * Fills the inventory with all buttons from the three priority sets.
     * <p>
     * Buttons are applied in priority order:
     * 1. Default buttons (lowest priority)
     * 2. Attached buttons (medium priority)
     * 3. Edit buttons (highest priority - overrides all others)
     * </p>
     *
     * @param inventory The inventory to fill
     * @param all       Whether to clear the inventory before filling.
     *                  If true, clears both the GUI inventory and player inventory (if enabled).
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    protected G fillItems(@NonNull Inventory inventory, @Nullable boolean all) {
        if (all) {
            inventory.clear();
            if (enablePlayerInventory) {
                player.getInventory().clear();
            }
        }
        Set<GuiButton> fillItems = getFillItems();
        fillItems.removeAll(editButtons);
        buttons.addAll(fillItems);
        for (GuiButton guiButton : fillItems) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                setItem(inventory, guiButton, button.getShowItem(player));
            }
        }

        Set<GuiButton> attachedItems = new HashSet<>(getAttachedItems());
        attachedItems.removeAll(editButtons);
        for (GuiButton guiButton : attachedItems) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                setItem(inventory, guiButton, button.getShowItem(player));
            }
        }

        for (GuiButton guiButton : editButtons) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                if (button != null) {
                    setItem(inventory, guiButton, button.getShowItem(player));
                } else {
                    setItem(inventory, guiButton, null);
                }
            }
        }
        return self();
    }

    /**
     * Sets an item in the inventory at the position specified by the GuiButton.
     * Delegates to the {@link me.huanmeng.gui.gui.interfaces.GuiHandler} for actual item placement.
     *
     * @param inventory The inventory to modify
     * @param guiButton The button defining the slot position
     * @param itemStack The ItemStack to place, or null to clear
     */
    private void setItem(@NotNull Inventory inventory, GuiButton guiButton, ItemStack itemStack) {
        if (!guiButton.isPlayerInventory()) {
            manager.guiHandler().onSetItem(this, inventory, guiButton, itemStack);
        } else if (enablePlayerInventory) {
            manager.guiHandler().onSetItem(this, player.getInventory(), guiButton, itemStack);
        }
    }

    /**
     * Refreshes specific slots in the inventory.
     * Only updates the display items for buttons at the specified slots.
     *
     * @param slots The slots to refresh
     * @return This GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G refresh(@NonNull Slots slots) {
        if (cacheInventory == null) {
            return self();
        }
        for (Slot slot : slots.slots(self())) {
            GuiButton button = getButton(slot);
            // Ignore it directly, there is no button in the first place, so there is no need for refresh
            if (button == null) {
                continue;
            }
            setItem(cacheInventory, button, button.getButton().getShowItem(player));
        }
        return self();
    }

    /**
     * Checks if a button can be placed for the current player.
     * Delegates to the button's placement condition.
     *
     * @param guiButton The button to check
     * @return true if the button can be placed, false otherwise
     */
    protected final boolean check(@NonNull GuiButton guiButton) {
        return guiButton.canPlace(player);
    }

    /**
     * Gets the cached inventory for this GUI.
     *
     * @return The cached inventory instance
     */
    @NonNull
    public Inventory getInventory() {
        return cacheInventory;
    }

    /**
     * Gets the size of the inventory in slots.
     *
     * @return The number of slots in the inventory
     */
    public int size() {
        return size;
    }

    /**
     * Checks if a click should be allowed for a specific button.
     * Delegates to the custom {@link GuiClick} handler if configured.
     *
     * @param player    The player who clicked
     * @param button    The button that was clicked
     * @param clickType The type of click
     * @param action    The inventory action
     * @param slotType  The type of slot clicked
     * @param slot      The slot index
     * @param hotBar    The hotbar button pressed (-1 if none)
     * @param e         The click event
     * @return true if the click should be processed, false to ignore it
     */
    public boolean allowClick(@NonNull Player player, @NonNull GuiButton button, @NonNull ClickType clickType,
                              @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBar,
                              @NonNull InventoryClickEvent e) {
        if (guiClick != null) {
            return guiClick.allowClick(player, button, clickType, action, slotType, slot, hotBar, e);
        }
        return true;
    }

    /**
     * Handles inventory click events for this GUI.
     * <p>
     * This method processes all click interactions including:
     * - Button clicks in the GUI inventory
     * - Clicks in the player's bottom inventory
     * - Item movement between inventories
     * - Hotbar key presses and offhand swaps
     * </p>
     *
     * @param e The inventory click event
     */
    public void onClick(@NonNull InventoryClickEvent e) {
        if (disableClick) {
            e.setCancelled(true);
            processingClickEvent = false;
            return;
        }
        processingClickEvent = true;
        Inventory inv = e.getClickedInventory();
        if (inv == null) {
            if (cancelClickOther) {
                e.setCancelled(true);
            }
            processingClickEvent = false;
            return;
        }
        InventoryView view = e.getView();
        if (Objects.equals(e.getClickedInventory(), this.cacheInventory) && Objects.equals(view.getTopInventory(), cacheInventory)) {
            int slot = e.getSlot();
            if (slot == -999) {
                e.setCancelled(true);
                processingClickEvent = false;
                return;
            }
            GuiButton guiButton = manager.guiHandler().queryClickButton(e, this);
            if (guiButton == null) {
                guiButton = getButton(slot, btn -> !btn.isPlayerInventory());
            }
            if (guiButton != null) {
                if (!allowClick(player, guiButton, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton(), e)) {
                    processingClickEvent = false;
                    return;
                }
                ClickData clickData = new ClickData(player, this, guiButton.getSlot(), null, guiButton.getButton(), e, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton());
                Result result = guiButton.onClick(clickData);
                if (result == null) {
                    result = Result.CANCEL;
                }
                if (result.isCancel()) {
                    e.setCancelled(true);
                }
                processResult(result, clickData);
            } else if (ItemUtil.isAir(e.getCurrentItem())) {
                if (e.getHotbarButton() >= 0 && cancelMoveHotBarItemToSelf && Objects.equals(e.getClickedInventory(), e.getView().getTopInventory())) {
                    e.setCancelled(true);
                }
                if (guiEmptyItemClick != null) {
                    if (guiEmptyItemClick.onClickEmptyButton(player, slot, e.getClick(), e.getAction(), e.getSlotType(), e.getHotbarButton(), e)) {
                        e.setCancelled(true);
                    }
                }
            } else {
                e.setCancelled(true);
            }
            if (!ItemUtil.isAir(e.getCursor()) && cancelMoveItemToSelf) {
                e.setCancelled(true);
            }
        } else if (Objects.equals(view.getBottomInventory(), e.getClickedInventory())) {
            if (cancelClickBottom) {
                e.setCancelled(true);
            }
            if (e.isShiftClick() && cancelMoveItemToSelf) {
                e.setCancelled(true);
            }
            if (enablePlayerInventory) {
                int slot = e.getSlot();
                if (slot == -999) {
                    e.setCancelled(true);
                    processingClickEvent = false;
                    return;
                }
                GuiButton guiButton = manager.guiHandler().queryClickButton(e, this);
                if (guiButton == null) {
                    guiButton = getButton(slot, GuiButton::isPlayerInventory);
                }
                if (guiButton != null) {
                    if (!allowClick(player, guiButton, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton(), e)) {
                        processingClickEvent = false;
                        return;
                    }
                    ClickData clickData = new ClickData(player, this, guiButton.getSlot(), null, guiButton.getButton(), e, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton());
                    Result result = guiButton.onClick(clickData);
                    if (result == null) {
                        result = Result.CANCEL;
                    }
                    if (result.isCancel()) {
                        e.setCancelled(true);
                    }
                    processResult(result, clickData);
                } else if (ItemUtil.isAir(e.getCurrentItem())) {
                    if (guiEmptyItemClick != null) {
                        if (guiEmptyItemClick.onClickEmptyButton(player, slot, e.getClick(), e.getAction(), e.getSlotType(), e.getHotbarButton(), e)) {
                            e.setCancelled(true);
                        }
                    }
                }
                if (!ItemUtil.isAir(e.getCursor()) && cancelMoveItemToBottom) {
                    e.setCancelled(true);
                }
            }

            if (guiBottomClick != null) {
                if (guiBottomClick.onClickBottom(player, view.getBottomInventory(), e.getClick(), e.getAction(), e.getSlotType(), e.getHotbarButton(), e)) {
                    e.setCancelled(true);
                }
            }
        } else {
            Component message = errorMessage.apply(e.getWhoClicked());
            if (message != null) {
                GuiManager.sendMessage(e.getWhoClicked(), message);
            } else {
                // 如果apply得到的是null?
                e.getWhoClicked().sendMessage("A inventory error occurred, please contact the administrator.");
            }
            // 出现错误, 直接关闭, 不做后续的返回parent之类的处理
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
        processingClickEvent = false;
    }

    protected void processResult(@NonNull Result result, @NonNull ClickData clickData) {
        InventoryClickEvent e = clickData.event;
        ItemStack itemStack = e.getCurrentItem();
        if (result.equals(Result.CANCEL)) {
            e.setCancelled(true);
        } else if (result.equals(Result.ALLOW)) {
            e.setCancelled(false);
        } else if (result.equals(Result.CLEAR)) {
            e.setCurrentItem(null);
        } else if (result.equals(Result.DECREMENT)) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            if (itemStack.getAmount() > 0) {
                e.setCurrentItem(itemStack);
            } else {
                e.setCurrentItem(null);
            }
        } else if (result.equals(Result.INCREMENTAL)) {
            itemStack.setAmount(itemStack.getAmount() + 1);
            e.setCurrentItem(itemStack);
        } else if (result.equals(Result.CANCEL_UPDATE)) {
            refresh(Slots.of(clickData.slotKey));
        } else if (result.equals(Result.CANCEL_UPDATE_ALL)) {
            refresh(true);
        } else if (result.equals(Result.CANCEL_CLOSE)) {
            close(true, false);
        } else if (result instanceof Result.Forward) {
            Result forwarded = ((Result.Forward) result).forwardClick(clickData);
            processResult(forwarded, clickData);
        } else if (customResultHandler != null) {
            customResultHandler.processResult(result, clickData);
        }
    }

    public void onDarg(@NonNull InventoryDragEvent e) {
        // 包装成InventoryClickEvent执行
        for (Map.Entry<Integer, ItemStack> entry : e.getNewItems().entrySet()) {
            // fake event
            InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(e.getView(), InventoryType.SlotType.CONTAINER, entry.getKey(), ClickType.LEFT, InventoryAction.UNKNOWN);
            ItemStack oldCursor = e.getOldCursor();
            inventoryClickEvent.setCursor(oldCursor);
            boolean hasCursor = ItemUtil.isAir(oldCursor);
            onClick(inventoryClickEvent);
            if (inventoryClickEvent.isCancelled()) {
                e.setCancelled(true);
            } else if (hasCursor) {
                e.setCursor(null);
            }
        }
    }

    @NonNull
    public GuiDraw<G> draw() {
        return new GuiDraw<>(self());
    }

    @NonNull
    protected abstract G self();

    @NonNull
    @CanIgnoreReturnValue
    public G title(@NonNull String title) {
        return title(LegacyComponentSerializer.legacySection().deserialize(title));
    }

    @NonNull
    @CanIgnoreReturnValue
    public G title(@NonNull Component title) {
        this.title = title;
        // 已经打开了? 那就先执行close后的操作然后再重新构建一个inventory打开
        if (!close) {
            onClose();
            openGui();
        }
        return self();
    }

    @NonNull
    @CanIgnoreReturnValue
    public G guiClick(@NonNull GuiClick guiClick) {
        this.guiClick = guiClick;
        return self();
    }

    @NonNull
    @CanIgnoreReturnValue
    public G whenClose(@NonNull Consumer<@NonNull G> consumer) {
        if (this.whenClose != null) {
            this.whenClose = this.whenClose.andThen(consumer);
        } else {
            this.whenClose = consumer;
        }
        return self();
    }

    @NonNull
    @CanIgnoreReturnValue
    public G backGui(@NonNull Function<@NonNull Player, @NonNull AbstractGui<?>> parentGuiGetter) {
        this.backGuiGetter = parentGuiGetter;
        return self();
    }

    @NonNull
    @CanIgnoreReturnValue
    public G backRunner(@NonNull Runnable runner) {
        backGuiRunner = runner;
        return self();
    }

    @NonNull
    @CanIgnoreReturnValue
    public G guiEmptyItemClick(@NonNull GuiEmptyItemClick guiEmptyItemClick) {
        this.guiEmptyItemClick = guiEmptyItemClick;
        return self();
    }

    @CanIgnoreReturnValue
    public G guiBottomClick(@NonNull GuiBottomClick guiBottomClick) {
        this.guiBottomClick = guiBottomClick;
        return self();
    }

    @CanIgnoreReturnValue
    public G setCancelClickOther(boolean cancelClickOther) {
        this.cancelClickOther = cancelClickOther;
        return self();
    }

    @CanIgnoreReturnValue
    public G setCancelClickBottom(boolean cancelClickBottom) {
        this.cancelClickBottom = cancelClickBottom;
        return self();
    }

    @CanIgnoreReturnValue
    public G setCancelMoveHotBarItemToSelf(boolean cancelMoveHotBarItemToSelf) {
        this.cancelMoveHotBarItemToSelf = cancelMoveHotBarItemToSelf;
        return self();
    }

    @CanIgnoreReturnValue
    public G setCancelMoveItemToSelf(boolean cancelMoveItemToSelf) {
        this.cancelMoveItemToSelf = cancelMoveItemToSelf;
        return self();
    }

    @CanIgnoreReturnValue
    public G setCancelMoveItemToBottom(boolean cancelMoveItemToBottom) {
        this.cancelMoveItemToBottom = cancelMoveItemToBottom;
        return self();
    }


    @CanIgnoreReturnValue
    public G setAllowReopen(boolean allowReopen) {
        this.allowReopen = allowReopen;
        return self();
    }

    @CanIgnoreReturnValue
    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    @CanIgnoreReturnValue
    public G manager(GuiManager manager) {
        this.manager = manager;
        return self();
    }

    @CanIgnoreReturnValue
    public G customResultHandler(CustomResultHandler customResultHandler) {
        this.customResultHandler = customResultHandler;
        return self();
    }

    public boolean enablePlayerInventory() {
        return enablePlayerInventory;
    }

    public G enablePlayerInventory(boolean enablePlayerInventory) {
        this.enablePlayerInventory = enablePlayerInventory;
        return self();
    }

    public GuiManager manager() {
        return this.manager;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public AbstractGui<G> copy() {
        AbstractGui<G> newed = newGui();
        return copy(newed);
    }

    protected abstract G newGui();

    protected AbstractGui<G> copy(Object newGui) {
        AbstractGui<G> gui = (AbstractGui<G>) newGui;
        gui.player = player;
        gui.title = title;
        gui.buttons = buttons;
        gui.attachedButtons = attachedButtons;
        gui.editButtons = editButtons;
        gui.backGuiGetter = backGuiGetter;
        gui.backGuiRunner = backGuiRunner;
        gui.cancelClickOther = cancelClickOther;
        gui.cancelClickBottom = cancelClickBottom;
        gui.cancelMoveHotBarItemToSelf = cancelMoveHotBarItemToSelf;
        gui.cancelMoveItemToSelf = cancelMoveItemToSelf;
        gui.cancelMoveItemToBottom = cancelMoveItemToBottom;
        gui.enablePlayerInventory = enablePlayerInventory;
        gui.disableClick = disableClick;
        gui.tickles = tickles;
        gui.intervalTick = intervalTick;
        gui.tickRefresh = tickRefresh;
        gui.guiClick = guiClick;
        gui.guiEmptyItemClick = guiEmptyItemClick;
        gui.guiBottomClick = guiBottomClick;
        gui.whenClose = whenClose;
        gui.errorMessage = errorMessage;
        gui.allowReopen = allowReopen;
        gui.metadata = metadata;
        gui.manager = manager;
        gui.customResultHandler = customResultHandler;
        return gui;
    }
}
