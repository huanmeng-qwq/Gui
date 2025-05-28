package me.huanmeng.gui.gui.impl;

import me.huanmeng.gui.gui.slot.Slots;
import me.huanmeng.gui.scheduler.Schedulers;
import me.huanmeng.gui.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/6/19<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class GuiWrappedInventory extends AbstractGuiCustom<GuiWrappedInventory> {
    protected Inventory inventory;

    public GuiWrappedInventory(@NonNull Player player, Inventory inventory) {
        super(player);
        this.inventory = inventory;
        cancelClickOther = false;
        cancelClickBottom = false;
        cancelMoveHotBarItemToSelf = false;
        cancelMoveItemToSelf = false;
        cancelMoveItemToBottom = false;
    }

    public GuiWrappedInventory() {
        super();
    }

    @Override
    protected @NonNull Inventory build(@NonNull InventoryHolder holder) {
        return InventoryUtil.createInventory(holder, inventory.getType(), inventory.getSize(), title);
    }

    @Override
    protected @NonNull GuiWrappedInventory fillItems(@NonNull Inventory inventory, boolean all) {
        if (all) {
            inventory.clear();
        }
        updateInventory(inventory);
        return self();
    }

    /**
     * 刷新指定的物品
     *
     * @deprecated 请使用 {@link #refresh(boolean)}
     */
    @Deprecated
    @Override
    public @NonNull GuiWrappedInventory refresh(@NonNull Slots slots) {
        return self();
    }

    @Override
    public void onClick(@NonNull InventoryClickEvent e) {
        super.onClick(e);
        setToInventory();
        Schedulers.sync().runLater(this::setToInventory, 1);
    }

    private void setToInventory() {
        if (cacheInventory == null) {
            return;
        }
        try {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, cacheInventory.getItem(i));
            }
        } catch (Exception ignored) {
        }
    }

    private void updateInventory(Inventory build) {
        for (int i = 0; i < inventory.getSize(); i++) {
            build.setItem(i, inventory.getItem(i));
        }
    }

    @Override
    protected @NonNull GuiWrappedInventory self() {
        return this;
    }

    @Override
    public GuiWrappedInventory copy() {
        return (GuiWrappedInventory) super.copy();
    }

    @Override
    protected GuiWrappedInventory newGui() {
        return new GuiWrappedInventory();
    }

    @Override
    protected GuiWrappedInventory copy(Object newGui) {
        GuiWrappedInventory copy = (GuiWrappedInventory) super.copy(newGui);
        copy.inventory = inventory;
        return copy;
    }
}
