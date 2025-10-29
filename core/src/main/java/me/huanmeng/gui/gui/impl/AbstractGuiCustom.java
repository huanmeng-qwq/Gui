package me.huanmeng.gui.gui.impl;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.scheduler.Schedulers;
import me.huanmeng.gui.util.InventoryUtil;
import me.huanmeng.gui.util.LocaleProvider;
import me.huanmeng.gui.util.item.ItemUtil;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Abstract base class for custom GUI implementations with configurable size.
 * <p>
 * AbstractGuiCustom provides the foundation for chest-style GUIs where the size
 * is determined by the number of lines (rows). Each line represents 9 slots, so
 * a 3-line GUI would have 27 total slots (3 rows × 9 columns).
 *
 * <p>
 * This class handles:
 * <ul>
 *     <li>GUI size configuration via {@link #line(int)}</li>
 *     <li>Building chest-type inventories with proper title rendering</li>
 *     <li>Opening inventories while preserving player cursor items</li>
 *     <li>Thread-safe GUI opening (handles both sync and async contexts)</li>
 *     <li>Proper cleanup on GUI close</li>
 * </ul>
 *
 * <p>
 * Implementations include {@link GuiCustom} and {@link GuiWrappedInventory}.
 *
 *
 * @param <G> the concrete GUI type for fluent method chaining
 * @author huanmeng_qwq
 * @since 2024/12/9
 * @see AbstractGui
 * @see GuiCustom
 * @see GuiWrappedInventory
 */
public abstract class AbstractGuiCustom<G extends AbstractGuiCustom<@NonNull G>> extends AbstractGui<@NonNull G> {
    /**
     * The number of lines (rows) in this GUI. Default is 6 (a double chest, 54 slots).
     * Each line represents 9 slots.
     */
    protected int line = 6;

    /**
     * Creates a new custom GUI for the specified player.
     *
     * @param player the player who will view this GUI
     */
    public AbstractGuiCustom(@NonNull Player player) {
        setPlayer(player);
    }

    /**
     * Creates a new custom GUI without a player.
     * <p>
     * The player must be set using {@link #setPlayer(Player)} before opening the GUI.
     *
     */
    public AbstractGuiCustom() {
    }

    /**
     * Sets the number of lines (rows) for this GUI.
     * <p>
     * Valid values are typically 1-6, where:
     * <ul>
     *     <li>1 line = 9 slots</li>
     *     <li>2 lines = 18 slots</li>
     *     <li>3 lines = 27 slots (single chest)</li>
     *     <li>4 lines = 36 slots</li>
     *     <li>5 lines = 45 slots</li>
     *     <li>6 lines = 54 slots (double chest)</li>
     * </ul>
     *
     *
     * @param line the number of lines/rows
     * @return this GUI instance for method chaining
     */
    @NonNull
    @CanIgnoreReturnValue
    public G line(int line) {
        this.line = line;
        return self();
    }

    /**
     * Opens this GUI for the configured player.
     * <p>
     * This method handles the complete GUI opening process:
     * <ol>
     *     <li>Validates that a player is set</li>
     *     <li>Initializes the GUI with title and size (line × 9)</li>
     *     <li>Builds the inventory with the GUI as holder</li>
     *     <li>Fills the inventory with buttons</li>
     *     <li>Preserves any items the player has on their cursor</li>
     *     <li>Opens the inventory for the player</li>
     *     <li>Caches the inventory state</li>
     *     <li>Re-fills items to prevent issues from close callbacks</li>
     * </ol>
     *
     * <p>
     * The method is thread-safe: if called from an async thread or during a click event,
     * it will schedule the GUI opening on the next tick to avoid concurrency issues.
     *
     *
     * @return this GUI instance for method chaining
     * @throws IllegalArgumentException if no player is set
     */
    @Override
    @NonNull
    public G openGui() {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        Runnable openInventory = () -> {
            init(title, line * 9);
            Inventory inventory = build(createHolder());
            fillItems(inventory, true);
            precache();
            // Ensure the player's cursor item is not lost
            ItemStack itemOnCursor = player.getItemOnCursor();
            if (!ItemUtil.isAir(itemOnCursor)) {
                player.setItemOnCursor(null);
            }
            player.openInventory(inventory);
            if (!ItemUtil.isAir(itemOnCursor)) {
                player.setItemOnCursor(itemOnCursor);
            }
            cache(inventory);
            fillItems(inventory, false);// Re-fill after opening to avoid clearing from close callback
        };
        if (processingClickEvent || manager.processingClickEvent() || !Bukkit.isPrimaryThread()) {
            Schedulers.sync().runLater(openInventory, 1);
        } else {
            openInventory.run();
        }
        return self();
    }

    /**
     * Builds a chest-type inventory with the configured title.
     * <p>
     * Creates a CHEST type inventory with the size determined by {@code line * 9}.
     * The title is rendered using Adventure's GlobalTranslator to support translatable
     * components based on the player's locale.
     *
     *
     * @param holder the inventory holder (this GUI instance)
     * @return the newly created chest inventory
     */
    @Override
    @NonNull
    protected Inventory build(@NonNull InventoryHolder holder) {
        return InventoryUtil.createInventory(holder, InventoryType.CHEST, line * 9, GlobalTranslator.render(title, LocaleProvider.locale(player)));
    }

    /**
     * Called when the GUI is closed by the player.
     * <p>
     * This method performs cleanup by calling the superclass close handler
     * and then clearing the cached inventory reference.
     *
     */
    @Override
    public void onClose() {
        super.onClose();
        unCache();
    }

    /**
     * Gets the total number of slots in this GUI.
     * <p>
     * If a cached inventory exists, returns its size. Otherwise, calculates
     * the size as {@code line * 9}.
     *
     *
     * @return the total number of slots
     */
    @Override
    public int size() {
        if (cacheInventory != null) {
            return cacheInventory.getSize();
        }
        return line * 9;
    }

    /**
     * Copies all properties from this GUI to the target GUI instance.
     * <p>
     * In addition to copying standard GUI properties from the superclass,
     * this also copies the line (row) configuration.
     *
     *
     * @param newGui the target GUI to copy properties to
     * @return the target GUI with copied properties
     */
    @SuppressWarnings("unchecked")
    @Override
    protected G copy(Object newGui) {
        super.copy(newGui);
        G guiCustom = (G) newGui;
        // GuiCustom
        guiCustom.line = line;
        return guiCustom;
    }

}
