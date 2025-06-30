package me.huanmeng.gui.util.item;

import me.huanmeng.gui.util.AdventureUtil;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 2023/8/24<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public final class ItemUtil {
    private static MethodHandle displayName;
    private static MethodHandle lore;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            displayName = lookup.unreflect(ItemMeta.class.getDeclaredMethod("displayName", AdventureUtil.componentClass));
            lore = lookup.unreflect(ItemMeta.class.getDeclaredMethod("lore", List.class));
        } catch (Exception ignored) {
        }
    }

    public static ItemBuilder builder(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    /**
     * 判断物品是否为空或空气
     */
    public static boolean isAir(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType().name().endsWith("_AIR");
    }

    /**
     * 判断物品类型是否相同
     */
    public static boolean isType(ItemStack itemStack, Material material) {
        if (material == null && itemStack == null) {
            return true;
        }
        return itemStack != null && itemStack.getType() == material;
    }

    /**
     * 获取物品附魔等级
     */
    public static int getEnchLv0(ItemStack itemStack, Enchantment enchantment) {
        if (isAir(itemStack)) {
            return 0;
        }
        return itemStack.getEnchantmentLevel(enchantment);
    }

    /**
     * 获取物品附魔等级
     */
    public static int getEnchLv0(ItemBuilder itemBuilder, Enchantment enchantment) {
        return getEnchLv0(itemBuilder.build(), enchantment);
    }

    /**
     * 获取物品附魔等级
     */
    public static int getEnchLv(ItemBuilder itemBuilder, Enchantment enchantment) {
        return getEnchLv0(itemBuilder, enchantment);
    }

    /**
     * 获取物品附魔等级
     */
    public static int getEnchLv(ItemStack itemStack, Enchantment enchantment) {
        return getEnchLv0(itemStack, enchantment);
    }

    /**
     * 判断物品是否有某个Flag
     */
    public static boolean hasFlag0(ItemStack itemStack, ItemFlag itemFlag) {
        return itemStack.getItemMeta().hasItemFlag(itemFlag);
    }

    /**
     * 判断物品是否有某个Flag
     */
    public static boolean hasFlag0(ItemBuilder itemBuilder, ItemFlag itemFlag) {
        return hasFlag0(itemBuilder.build(), itemFlag);
    }

    /**
     * 判断物品是否有某个Flag
     */
    public static boolean hasFlag(ItemBuilder itemBuilder, ItemFlag itemFlag) {
        return hasFlag0(itemBuilder, itemFlag);
    }

    /**
     * 判断物品是否有某个Flag
     */
    public static boolean hasFlag(ItemStack itemStack, ItemFlag itemFlag) {
        return hasFlag0(itemStack, itemFlag);
    }

    /**
     * 获取物品的Lore
     */
    public static List<String> getLores(ItemStack itemStack) {
        if (isAir(itemStack)) {
            return Collections.emptyList();
        }
        if (itemStack.hasItemMeta() || itemStack.getItemMeta() != null) {
            return itemStack.getItemMeta().getLore() != null ? itemStack.getItemMeta().getLore() : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public static void setName(ItemStack itemStack, Component name) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (displayName != null) {
            try {
                displayName.bindTo(itemMeta).invoke(AdventureUtil.toComponent(name));
                itemStack.setItemMeta(itemMeta);
                return;
            } catch (Throwable ignored) {
            }
        }
        itemMeta.setDisplayName(AdventureUtil.toLegacyString(name));
        itemStack.setItemMeta(itemMeta);
    }

    public static void setLore(ItemStack itemStack, List<Component> lores) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (lore != null) {
            try {
                lore.bindTo(itemMeta).invoke(lores.stream().map(AdventureUtil::toComponent).collect(Collectors.toList()));
                itemStack.setItemMeta(itemMeta);
                return;
            } catch (Throwable ignored) {
            }
        }
        itemMeta.setLore(lores.stream().map(AdventureUtil::toLegacyString).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
    }
}