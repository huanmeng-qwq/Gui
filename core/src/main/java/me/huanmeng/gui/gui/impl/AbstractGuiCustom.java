package me.huanmeng.gui.gui.impl;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.scheduler.Schedulers;
import me.huanmeng.gui.util.InventoryUtil;
import me.huanmeng.gui.util.item.ItemUtil;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
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
    @CanIgnoreReturnValue
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
            fillItems(inventory, true);
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
            fillItems(inventory, false);// 打开后重新填充一遍，避免call之前的close函数时清除了player背包内容
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
        return InventoryUtil.createInventory(holder, InventoryType.CHEST, line * 9, title);
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
