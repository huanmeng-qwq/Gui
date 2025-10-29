package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.button.function.PlayerClickInterface
import me.huanmeng.gui.gui.enums.Result
import org.bukkit.inventory.ItemStack

/**
 * Extension function to convert an [ItemStack] to a [Button] with a click handler.
 *
 * Provides a quick way to create an interactive button from an ItemStack without using the full DSL.
 * The button will display this ItemStack and execute the provided handler when clicked.
 *
 * Example:
 * ```kotlin
 * val closeButton = ItemStack(Material.BARRIER).onClick {
 *     player.closeInventory()
 * }
 * ```
 *
 * @param result The click result behavior (defaults to CANCEL to prevent item pickup)
 * @param block The click handler that receives ClickData for detailed event information
 * @return A Button that displays this ItemStack and handles clicks
 * @see Result
 * @see ClickData
 */
fun ItemStack.onClick(result: Result = Result.CANCEL, block: (ClickData.() -> Unit)): Button {
    return buildButton {
        item = this@onClick
        click = PlayerClickInterface {
            block.invoke(it)
            return@PlayerClickInterface result
        }
    }
}