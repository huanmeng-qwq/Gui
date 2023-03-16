package me.huanmeng.opensource.bukkit.util.item

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
fun ItemStack.builder(): ItemBuilder {
    return ItemBuilder(this)
}

object ItemUtil {
    @JvmStatic
    fun isAir(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack.type == Material.AIR
    }

    @JvmStatic
    fun isType(itemStack: ItemStack?, material: Material?): Boolean {
        return if (material == null && itemStack == null) {
            true
        } else itemStack != null && itemStack.type == material
    }

    @JvmStatic
    fun getEnchLv0(itemStack: ItemStack, enchantment: Enchantment): Int {
        if (isAir(itemStack)) {
            return 0
        }
        return itemStack.getEnchantmentLevel(enchantment)
    }

    @JvmStatic
    fun getEnchLv0(itemBuilder: ItemBuilder, enchantment: Enchantment): Int {
        return getEnchLv0(itemBuilder.build(), enchantment)
    }

    fun ItemBuilder.getEnchLv(enchantment: Enchantment): Int {
        return getEnchLv0(this, enchantment)
    }

    fun ItemStack.getEnchLv(enchantment: Enchantment): Int {
        return getEnchLv0(this, enchantment)
    }

    @JvmStatic
    fun hasFlag0(itemStack: ItemStack, itemFlag: ItemFlag): Boolean {
        return itemStack.itemMeta.hasItemFlag(itemFlag)
    }

    @JvmStatic
    fun hasFlag0(itemBuilder: ItemBuilder, itemFlag: ItemFlag): Boolean {
        return hasFlag0(itemBuilder.build(), itemFlag)
    }

    fun ItemBuilder.hasFlag(itemFlag: ItemFlag): Boolean {
        return hasFlag0(this, itemFlag)
    }

    fun ItemStack.hasFlag(itemFlag: ItemFlag): Boolean {
        return hasFlag0(this, itemFlag)
    }

    fun ItemStack.getLores(): List<String?> {
        if (isAir(this)) {
            return emptyList()
        }
        if (hasItemMeta() || itemMeta != null) {
            return itemMeta.lore ?: emptyList()
        }
        return emptyList()
    }
}