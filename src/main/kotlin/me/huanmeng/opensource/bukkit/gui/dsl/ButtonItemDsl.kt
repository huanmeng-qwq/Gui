@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.button.function.PlayerItemInterface
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun buildButtonItem(lambda: (player: Player) -> ItemStack): PlayerItemInterface {
    return PlayerItemInterface(lambda)
}

fun buildButtonItem(itemStack: ItemStack): PlayerItemInterface {
    return PlayerItemInterface { itemStack }
}