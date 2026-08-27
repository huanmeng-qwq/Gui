package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.api.render.ButtonRenderOperation;
import me.huanmeng.gui.api.render.ClearSlotRenderOperation;
import me.huanmeng.gui.api.render.RenderOperation;
import me.huanmeng.gui.api.render.RenderPlan;
import me.huanmeng.gui.bukkit.api.BukkitButton;
import me.huanmeng.gui.bukkit.api.BukkitInventoryAdapter;
import me.huanmeng.gui.bukkit.api.BukkitItemAdapter;
import me.huanmeng.gui.bukkit.api.BukkitViewerFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

final class BukkitInventoryApplier {
    private final BukkitInventoryAdapter inventories;
    private final BukkitItemAdapter items;
    private final BukkitViewerFactory viewerFactory;

    BukkitInventoryApplier(BukkitInventoryAdapter inventories, BukkitItemAdapter items, BukkitViewerFactory viewerFactory) {
        this.inventories = inventories;
        this.items = items;
        this.viewerFactory = viewerFactory;
    }

    void apply(RenderPlan plan, Inventory inventory, Player viewer) {
        if (plan == null || inventory == null || viewer == null) throw new NullPointerException();
        for (RenderOperation operation : plan.operations()) {
//            System.out.println("executing " + operation +" for " + viewer.getName());
            if (operation instanceof ButtonRenderOperation) {
                ButtonRenderOperation button = (ButtonRenderOperation) operation;
                if (button.button() instanceof BukkitButton) {
                    ItemStack item = ((BukkitButton) button.button()).display(viewerFactory.create(viewer)).item();
                    inventories.set(inventory, button.slot().index(), items.copyForInventory(item));
                }
            } else if (operation instanceof ClearSlotRenderOperation) {
                inventories.clear(inventory, ((ClearSlotRenderOperation) operation).slot().index());
            }
        }
    }
}
