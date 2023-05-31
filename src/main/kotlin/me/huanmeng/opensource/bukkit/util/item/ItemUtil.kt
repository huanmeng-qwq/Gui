package me.huanmeng.opensource.bukkit.util.item

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * 将ItemStack转换为ItemBuilder
 */
fun ItemStack.builder(): ItemBuilder {
    return ItemBuilder(this)
}

object ItemUtil {
    /**
     * 判断物品是否为空或空气
     */
    @JvmStatic
    fun isAir(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack.type == Material.AIR
    }

    /**
     * 判断物品类型是否相同
     */
    @JvmStatic
    fun isType(itemStack: ItemStack?, material: Material?): Boolean {
        return if (material == null && itemStack == null) {
            true
        } else itemStack != null && itemStack.type == material // kotlin的==相当于java的equals ===相当于java的==
    }

    /**
     * 获取物品附魔等级
     */
    @JvmStatic
    fun getEnchLv0(itemStack: ItemStack, enchantment: Enchantment): Int {
        if (isAir(itemStack)) {
            return 0
        }
        return itemStack.getEnchantmentLevel(enchantment)
    }

    /**
     * 获取物品附魔等级
     */
    @JvmStatic
    fun getEnchLv0(itemBuilder: ItemBuilder, enchantment: Enchantment): Int {
        return getEnchLv0(itemBuilder.build(), enchantment)
    }

    /**
     * 获取物品附魔等级
     */
    @JvmStatic
    fun ItemBuilder.getEnchLv(enchantment: Enchantment): Int {
        return getEnchLv0(this, enchantment)
    }

    /**
     * 获取物品附魔等级
     */
    @JvmStatic
    fun ItemStack.getEnchLv(enchantment: Enchantment): Int {
        return getEnchLv0(this, enchantment)
    }

    /**
     * 判断物品是否有某个Flag
     */
    @JvmStatic
    fun hasFlag0(itemStack: ItemStack, itemFlag: ItemFlag): Boolean {
        return itemStack.itemMeta!!.hasItemFlag(itemFlag)
    }

    /**
     * 判断物品是否有某个Flag
     */
    @JvmStatic
    fun hasFlag0(itemBuilder: ItemBuilder, itemFlag: ItemFlag): Boolean {
        return hasFlag0(itemBuilder.build(), itemFlag)
    }

    /**
     * 判断物品是否有某个Flag
     */
    @JvmStatic
    fun ItemBuilder.hasFlag(itemFlag: ItemFlag): Boolean {
        return hasFlag0(this, itemFlag)
    }

    /**
     * 判断物品是否有某个Flag
     */
    @JvmStatic
    fun ItemStack.hasFlag(itemFlag: ItemFlag): Boolean {
        return hasFlag0(this, itemFlag)
    }

    /**
     * 获取物品的Lore
     */
    @JvmStatic
    fun ItemStack.getLores(): List<String> {
        if (isAir(this)) {
            return emptyList()
        }
        if (hasItemMeta() || itemMeta != null) {
            return itemMeta!!.lore ?: emptyList()
        }
        return emptyList()
    }
}