@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.button.function.PlayerItemInterface
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 构建[PlayerItemInterface]
 */
fun buildButtonItem(lambda: (player: Player) -> ItemStack): PlayerItemInterface {
    return PlayerItemInterface(lambda)
}

/**
 * 构建[PlayerItemInterface]
 */
fun buildButtonItem(itemStack: ItemStack): PlayerItemInterface {
    return PlayerItemInterface { itemStack }
}