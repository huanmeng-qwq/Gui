@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.button.function.ItemProvider
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Builds a [ItemProvider] using a player-dependent item supplier.
 *
 * This creates an item interface where the displayed ItemStack can vary based on the viewing player.
 * Useful for creating personalized button displays (e.g., showing player-specific statistics).
 *
 * @param lambda Function that receives a player and returns the ItemStack to display
 * @return A ItemProvider that supplies player-dependent items
 */
fun buildButtonItem(lambda: (player: Player) -> ItemStack): ItemProvider {
    return ItemProvider(lambda)
}

/**
 * Builds a [ItemProvider] with a static ItemStack.
 *
 * This creates an item interface where the displayed ItemStack is the same for all players.
 * The item will be wrapped in a lambda for compatibility with the ItemProvider API.
 *
 * @param itemStack The ItemStack to display for all players
 * @return A ItemProvider that supplies the same item to all players
 */
fun buildButtonItem(itemStack: ItemStack): ItemProvider {
    return ItemProvider { itemStack }
}