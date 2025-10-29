@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.function.PlayerItemInterface
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Builds a [PlayerItemInterface] using a player-dependent item supplier.
 *
 * This creates an item interface where the displayed ItemStack can vary based on the viewing player.
 * Useful for creating personalized button displays (e.g., showing player-specific statistics).
 *
 * @param lambda Function that receives a player and returns the ItemStack to display
 * @return A PlayerItemInterface that supplies player-dependent items
 */
fun buildButtonItem(lambda: (player: Player) -> ItemStack): PlayerItemInterface {
    return PlayerItemInterface(lambda)
}

/**
 * Builds a [PlayerItemInterface] with a static ItemStack.
 *
 * This creates an item interface where the displayed ItemStack is the same for all players.
 * The item will be wrapped in a lambda for compatibility with the PlayerItemInterface API.
 *
 * @param itemStack The ItemStack to display for all players
 * @return A PlayerItemInterface that supplies the same item to all players
 */
fun buildButtonItem(itemStack: ItemStack): PlayerItemInterface {
    return PlayerItemInterface { itemStack }
}