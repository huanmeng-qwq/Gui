package me.huanmeng.gui.gui.impl;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.huanmeng.gui.adventure.ComponentConvert;
import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiManager;
import me.huanmeng.gui.util.item.ItemUtil;
import me.huanmeng.gui.scheduler.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * 2024/12/9<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public abstract class AbstractGuiCustom<G extends AbstractGuiCustom<@NonNull G>> extends AbstractGui<@NonNull G> {
    private static Class<?> componentClass;
    private static MethodHandle paperCreateTitle;

    static {
        try {
            componentClass = Class.forName("net{}kyori{}adventure{}text{}Component".replace("{}", "."));
            paperCreateTitle = MethodHandles.lookup().unreflect(Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, int.class, componentClass));
        } catch (Exception ignored) {
        }
    }


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
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        if (paperCreateTitle == null || componentClass == null || !componentClass.isInstance(title)) {
            return createSpigotInventory(holder);
        }
        try {
            return (Inventory) paperCreateTitle.invoke(holder, line * 9, title);
        } catch (Throwable e) {
            return createSpigotInventory(holder);
        }
    }

    protected @NotNull Inventory createSpigotInventory(@NotNull InventoryHolder holder) {
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
