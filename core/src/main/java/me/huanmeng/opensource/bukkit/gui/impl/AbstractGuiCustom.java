package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.component.ComponentConvert;
import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiManager;
import me.huanmeng.opensource.bukkit.util.item.ItemUtil;
import me.huanmeng.opensource.scheduler.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/12/9<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public abstract class AbstractGuiCustom<G extends AbstractGuiCustom<@NonNull G>> extends AbstractGui<@NonNull G> {
    protected int line = 6;

    public AbstractGuiCustom(@NonNull Player player) {
        setPlayer(player);
    }

    public AbstractGuiCustom() {
    }

    @NonNull
    public G line(int line) {
        this.line = line;
        return self();
    }

    @Override
    @NonNull
    public G openGui() {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        Runnable openInventory = () -> {
            init(title, line * 9);
            Inventory inventory = build(createHolder());
            fillItems(inventory, false);
            precache();
            // 确保玩家鼠标上的物品不会变
            ItemStack itemOnCursor = player.getItemOnCursor();
            if (!ItemUtil.isAir(itemOnCursor)) {
                player.setItemOnCursor(null);
            }
            player.openInventory(inventory);
            if (!ItemUtil.isAir(itemOnCursor)) {
                player.setItemOnCursor(itemOnCursor);
            }
            cache(inventory);
        };
        if (processingClickEvent || manager.processingClickEvent() || !Bukkit.isPrimaryThread()) {
            Schedulers.sync().runLater(openInventory, 1);
        } else {
            openInventory.run();
        }
        return self();
    }

    @Override
    @NonNull
    protected Inventory build(@NonNull InventoryHolder holder) {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        ComponentConvert componentConvert = GuiManager.instance().componentConvert();
        return Bukkit.createInventory(holder, line * 9, componentConvert.convert(player, title));
    }

    @Override
    public void onClose() {
        super.onClose();
        unCache();
    }

    @Override
    public int size() {
        if (cacheInventory != null) {
            return cacheInventory.getSize();
        }
        return line * 9;
    }

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
