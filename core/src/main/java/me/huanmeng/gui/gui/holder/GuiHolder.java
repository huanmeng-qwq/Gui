package me.huanmeng.gui.gui.holder;

import me.huanmeng.gui.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Custom inventory holder implementation that associates a Bukkit inventory with a GUI instance.
 *
 * <p>This class implements {@link InventoryHolder} and serves as a bridge between
 * Bukkit's inventory system and this library's GUI abstraction. Each GuiHolder
 * maintains references to:
 * <ul>
 *   <li>The player viewing the GUI</li>
 *   <li>The underlying Bukkit inventory</li>
 *   <li>The AbstractGui instance managing the GUI logic</li>
 * </ul>
 *
 * <p><b>Usage:</b>
 * <br>GuiHolder instances are created internally by the library and attached to
 * Bukkit inventories. You can retrieve the GuiHolder from an inventory to access
 * the associated GUI:
 *
 * <pre>{@code
 * Inventory inventory = ...;
 * InventoryHolder holder = inventory.getHolder();
 * if (holder instanceof GuiHolder) {
 *     GuiHolder<?> guiHolder = (GuiHolder<?>) holder;
 *     AbstractGui<?> gui = guiHolder.gui();
 *     Player player = guiHolder.player();
 * }
 * }</pre>
 *
 * @param <G> the type of AbstractGui this holder manages
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
public class GuiHolder<@NonNull G extends AbstractGui<G>> implements InventoryHolder {
    /**
     * The player who is viewing this GUI.
     */
    @NonNull
    private final Player player;

    /**
     * The Bukkit inventory associated with this GUI.
     */
    private Inventory inventory;

    /**
     * The AbstractGui instance managing this GUI's logic and state.
     */
    @NonNull
    private final G gui;

    /**
     * Creates a new GuiHolder.
     *
     * @param player the player viewing the GUI
     * @param gui the AbstractGui instance managing the GUI
     */
    public GuiHolder(@NonNull Player player, @NonNull G gui) {
        this.player = player;
        this.gui = gui;
    }

    /**
     * Returns the player viewing this GUI.
     *
     * @return the player
     */
    @NonNull
    public Player player() {
        return player;
    }

    /**
     * Returns the Bukkit inventory associated with this GUI.
     *
     * @return the inventory
     */
    @NonNull
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Returns the AbstractGui instance managing this GUI.
     *
     * @return the GUI instance
     */
    @NonNull
    public G gui() {
        return gui;
    }

    /**
     * Sets the Bukkit inventory for this holder.
     *
     * <p>Called internally by the library when creating the inventory.
     *
     * @param inventory the inventory to associate with this holder
     */
    public void setInventory(@NonNull Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Returns the inventory held by this holder.
     *
     * <p>Implementation of {@link InventoryHolder#getInventory()}.
     *
     * @return the inventory
     */
    @Override
    @NonNull
    public Inventory getInventory() {
        return inventory;
    }
}
