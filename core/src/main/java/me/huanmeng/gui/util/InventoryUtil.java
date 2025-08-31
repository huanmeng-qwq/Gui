package me.huanmeng.gui.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InventoryUtil {
    private static MethodHandle componentCreateChest;
    private static MethodHandle componentCreateType;

    static {
        try {
            componentCreateChest = MethodHandles.lookup().unreflect(Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, int.class, AdventureUtil.class));
            componentCreateType = MethodHandles.lookup().unreflect(Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, InventoryType.class, AdventureUtil.componentClass));
        } catch (Exception ignored) {
        }
    }

    public static Inventory createInventory(InventoryHolder holder, InventoryType type, int line, Component title) {
        if (componentCreateChest == null || AdventureUtil.componentClass == null) {
            return createSpigotInventory(holder, type, line, title);
        }
        try {
            if (type == InventoryType.CHEST) {
                return (Inventory) componentCreateChest.invoke(holder, line * 9, AdventureUtil.toComponent(title));
            } else {
                return (Inventory) componentCreateType.invoke(holder, type, AdventureUtil.toComponent(title));
            }
        } catch (Throwable e) {
            return createSpigotInventory(holder, type, line, title);
        }
    }

    private static @NotNull Inventory createSpigotInventory(@NotNull InventoryHolder holder, InventoryType type, int line, Component titleComponent) {
        final String title = AdventureUtil.toLegacyString(titleComponent);
        if (type == InventoryType.CHEST) {
            return Bukkit.createInventory(holder, line * 9, title);
        }
        return Bukkit.createInventory(holder, type, title);
    }

}
