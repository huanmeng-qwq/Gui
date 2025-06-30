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
    private static Class<?> componentClass;
    private static MethodHandle componentCreateChest;
    private static MethodHandle componentCreateType;

    static {
        try {
            componentClass = Class.forName("net{}kyori{}adventure{}text{}Component".replace("{}", "."));
            componentCreateChest = MethodHandles.lookup().unreflect(Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, int.class, componentClass));
            componentCreateType = MethodHandles.lookup().unreflect(Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, InventoryType.class, componentClass));
        } catch (Exception ignored) {
        }
    }

    public static Inventory createInventory(InventoryHolder holder, InventoryType type, int line, Component title) {
        if (componentCreateChest == null || componentClass == null || /*maybe relocate?*/ !componentClass.isInstance(title)) {
            return createSpigotInventory(holder, type, line, title);
        }
        try {
            if (type == InventoryType.CHEST) {
                return (Inventory) componentCreateChest.invoke(holder, line * 9, title);
            } else {
                return (Inventory) componentCreateType.invoke(holder, type, title);
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
