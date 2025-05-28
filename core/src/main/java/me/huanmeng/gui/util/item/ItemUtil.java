package me.huanmeng.gui.util.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * 2023/8/24<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public final class ItemUtil {

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

}