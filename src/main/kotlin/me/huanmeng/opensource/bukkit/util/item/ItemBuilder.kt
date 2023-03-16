package me.huanmeng.opensource.bukkit.util.item

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.map.MapView
import org.bukkit.potion.PotionEffect
import java.util.*
import kotlin.random.Random

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
open class ItemBuilder : Cloneable {
    private var `is`: ItemStack

    constructor(`is`: ItemStack) {
        this.`is` = `is`.clone()
    }

    constructor(`is`: ItemStack, name: String?) {
        this.`is` = `is`
        this.setName(name)
    }

    constructor(m: Material?, name: String?) {
        `is` = ItemStack(m)
        this.setName(name)
    }

    @JvmOverloads
    constructor(m: Material?, amount: Int = 1) {
        `is` = ItemStack(m, amount)
    }

    constructor(m: Material?, amount: Int, durability: Byte) {
        `is` = ItemStack(m, amount, durability.toShort())
    }

    constructor(type: Material?, amount: Int, durability: Short) {
        `is` = ItemStack(type, amount, durability)
    }

    override fun clone(): ItemBuilder {
        return ItemBuilder(`is`)
    }

    open fun setDurability(dur: Short): ItemBuilder {
        `is`.durability = dur
        return this
    }

    open fun setDurability(dur: Int): ItemBuilder {
        `is`.durability = dur.toShort()
        return this
    }

    open fun setAmount(amount: Int): ItemBuilder {
        `is`.amount = amount
        return this
    }

    open fun setName(str: String?): ItemBuilder {
        var name = str
        val im = `is`.itemMeta
        if (name != null && name.isEmpty()) {
            name = "§c"
        }
        if (name == null) {
            im.displayName = null
        } else {
            im.displayName = ChatColor.translateAlternateColorCodes('&', name)
        }
        `is`.itemMeta = im
        return this
    }

    open fun setNameUnColor(name: String?): ItemBuilder {
        var name = name
        val im = `is`.itemMeta
        if (name != null && name.isEmpty()) {
            name = "§c"
        }
        if (name == null) {
            im.displayName = null
        } else {
            im.displayName = name
        }
        `is`.itemMeta = im
        return this
    }

    val name: String?
        get() {
            val im: ItemMeta? = `is`.itemMeta
            return im?.displayName
        }

    open fun addFlag(vararg flag: ItemFlag?): ItemBuilder {
        val im = `is`.itemMeta
        im.addItemFlags(*flag)
        `is`.itemMeta = im
        return this
    }

    open fun removeFlag(vararg flag: ItemFlag?): ItemBuilder {
        val im = `is`.itemMeta
        im.removeItemFlags(*flag)
        `is`.itemMeta = im
        return this
    }

    open fun addUnsafeEnchantment(ench: Enchantment?, level: Int): ItemBuilder {
        return if (`is`.type == Material.ENCHANTED_BOOK) {
            val im = `is`.itemMeta as EnchantmentStorageMeta
            im.addStoredEnchant(ench, level, true)
            `is`.itemMeta = im
            this
        } else {
            `is`.addUnsafeEnchantment(ench, level)
            this
        }
    }

    open fun addRandomUnsafeEnchantment(level: Int, vararg ench: Enchantment?): ItemBuilder {
        `is`.addUnsafeEnchantment(ench[Random.nextInt(ench.size)], level)
        return this
    }

    open fun clearLores(): ItemBuilder {
        val im = `is`.itemMeta
        im.lore = ArrayList()
        `is`.itemMeta = im
        return this
    }

    open fun removeEnchantment(ench: Enchantment?): ItemBuilder {
        `is`.removeEnchantment(ench)
        return this
    }

    open fun removeEnchantments(): ItemBuilder {
        val im = `is`.itemMeta
        im.enchants.forEach { (enchantment: Enchantment?, integer: Int?) -> im.removeEnchant(enchantment) }
        return this
    }

    open fun setSkullOwner(owner: String?): ItemBuilder {
        try {
            val im = `is`.itemMeta as SkullMeta
            im.owner = owner
            `is`.itemMeta = im
        } catch (_: ClassCastException) {
        }
        return this
    }

    open fun addEnchant(ench: Enchantment?, level: Int): ItemBuilder {
        return if (`is`.type == Material.ENCHANTED_BOOK) {
            val im = `is`.itemMeta as EnchantmentStorageMeta
            im.addStoredEnchant(ench, level, true)
            `is`.itemMeta = im
            this
        } else {
            val im = `is`.itemMeta
            im.addEnchant(ench, level, true)
            `is`.itemMeta = im
            this
        }
    }

    open fun addEnchantments(enchantments: Map<Enchantment?, Int?>?): ItemBuilder {
        `is`.addEnchantments(enchantments)
        return this
    }

    open fun setInfinityDurability(): ItemBuilder {
        `is`.durability = 32767.toShort()
        return this
    }

    open fun setUnbreakable(): ItemBuilder {
        val im = `is`.itemMeta
        im.spigot().isUnbreakable = true
        `is`.itemMeta = im
        return this
    }

    open fun setType(material: Material?): ItemBuilder {
        `is`.type = material
        return this
    }

    open fun setLore(vararg lore: String): ItemBuilder {
        val im = `is`.itemMeta
        im.lore = listOf(elements = lore)
        `is`.itemMeta = im
        return this
    }

    open fun setLore(lore: List<String?>?): ItemBuilder {
        if (lore == null) {
            return this
        }
        val im = `is`.itemMeta
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    open fun removeLoreLine(line: String?): ItemBuilder {
        val im = `is`.itemMeta
        val lore: MutableList<String?> = ArrayList(im.lore)
        return if (!lore.contains(line)) {
            this
        } else {
            lore.remove(line)
            im.lore = lore
            `is`.itemMeta = im
            this
        }
    }

    open fun removeLoreLine(index: Int): ItemBuilder {
        val im = `is`.itemMeta
        val lore: MutableList<String?> = ArrayList(im.lore)
        return if (index >= 0 && index <= lore.size) {
            lore.removeAt(index)
            im.lore = lore
            `is`.itemMeta = im
            this
        } else {
            this
        }
    }

    open fun addLoreLine(line: String?): ItemBuilder {
        val im = `is`.itemMeta
        var lore: MutableList<String?> = ArrayList()
        if (im.hasLore()) {
            lore = ArrayList(im.lore)
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', line))
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    open fun addLoreLine(line: String, pos: Int): ItemBuilder {
        val im = `is`.itemMeta
        val lore: MutableList<String> = ArrayList(im.lore)
        lore.add(pos, line)
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    val lores: Int
        get() {
            val im = `is`.itemMeta
            return if (im.hasLore()) im.lore.size else 0
        }

    open fun addLoreLine(vararg lines: String?): ItemBuilder {
        for (line in lines) {
            this.addLoreLine(line)
        }
        return this
    }

    open fun setDyeColor(color: DyeColor): ItemBuilder {
        `is`.durability = color.dyeData.toShort()
        return this
    }

    @Deprecated("")
    open fun setWoolColor(color: DyeColor): ItemBuilder {
        if (`is`.type == Material.WOOL) {
            `is`.durability = color.woolData.toShort()
        }
        return this
    }

    open fun setLeatherArmorColor(color: Color?): ItemBuilder {
        try {
            val im = `is`.itemMeta as LeatherArmorMeta
            im.color = color
            `is`.itemMeta = im
        } catch (ignored: ClassCastException) {
        }
        return this
    }

    open fun setMapView(mapView: MapView): ItemBuilder {
        this.setDurability(mapView.id)
        return this
    }

    open fun addPotion(potionEffect: PotionEffect?): ItemBuilder {
        val im = `is`.itemMeta as PotionMeta
        im.addCustomEffect(potionEffect, true)
        `is`.itemMeta = im
        return this
    }

    open fun build(): ItemStack {
        return `is`
    }

    open fun addBannerPattern(pattern: Pattern?): ItemBuilder {
        val bannerMeta = `is`.itemMeta as BannerMeta
        bannerMeta.addPattern(pattern)
        `is`.itemMeta = bannerMeta
        return this
    }

    open fun setBannerBaseColor(color: DyeColor?): ItemBuilder {
        val bannerMeta = `is`.itemMeta as BannerMeta
        bannerMeta.baseColor = color
        `is`.itemMeta = bannerMeta
        return this
    }

    fun addLore(vararg s: String): ItemBuilder {
        return this.addLoreLine(lines = s)
    }

    fun glow(): ItemBuilder {
        return addEnchant(Enchantment.DURABILITY, 1).addFlag(ItemFlag.HIDE_ENCHANTS)
    }

    fun line() {
        addLore("§h§u§a§n§m§e§n§g")
    }

    val type: Material
        get() = `is`.type

    fun copy(): ItemBuilder {
        return ItemBuilder(`is`.clone())
    }
}