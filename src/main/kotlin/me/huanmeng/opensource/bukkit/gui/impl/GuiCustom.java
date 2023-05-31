package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.util.item.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class GuiCustom extends AbstractGui<@NonNull GuiCustom> {
    protected int line = 6;

    public GuiCustom(@NonNull Player player) {
        setPlayer(player);
    }

    @NonNull
    public GuiCustom line(int line) {
        this.line = line;
        return this;
    }

    @Override
    @NonNull
    public GuiCustom openGui() {
        init(title, line * 9);
        Inventory inventory = build(createHolder());
        fillItems(inventory, false);
        precache();
        // 保证用户鼠标上的物品不会便
        ItemStack itemOnCursor = player.getItemOnCursor();
        if (!ItemUtil.isAir(itemOnCursor)) {
            player.setItemOnCursor(null);
        }
        player.openInventory(inventory);
        if (!ItemUtil.isAir(itemOnCursor)) {
            player.setItemOnCursor(itemOnCursor);
        }
        cache(inventory);
        return self();
    }

    @Override
    @NonNull
    protected Inventory build(@NonNull InventoryHolder holder) {
        return Bukkit.createInventory(holder, line * 9, title);
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

    @Override
    @NonNull
    protected GuiCustom self() {
        return this;
    }
}
