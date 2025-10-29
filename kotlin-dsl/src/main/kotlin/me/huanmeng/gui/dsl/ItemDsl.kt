package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.button.function.PlayerClickInterface
import me.huanmeng.gui.gui.enums.Result
import org.bukkit.Material
import org.bukkit.entity.Player
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

/**
 * Creates a simple click button with a player-only handler.
 *
 * This is a simplified version of [onClick] that only receives the player,
 * making it easier to write simple click handlers.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).onPlayerClick { player ->
 *     player.sendMessage("You clicked a diamond!")
 * }
 * ```
 *
 * @param result The click result behavior (defaults to CANCEL_UPDATE)
 * @param block The click handler that receives only the player
 * @return A Button with the click handler
 */
fun ItemStack.onPlayerClick(result: Result = Result.CANCEL_UPDATE, block: (Player) -> Unit): Button {
    return buildButton {
        item = this@onPlayerClick
        when (result) {
            Result.CANCEL -> cancelClick(block)
            Result.CANCEL_UPDATE -> updateClick(block)
            Result.CANCEL_UPDATE_ALL -> updateAllClick(block)
            else -> cancelClick(block)
        }
    }
}

/**
 * Creates a button that displays different items for different players.
 *
 * Example:
 * ```kotlin
 * val button = dynamicItem { player ->
 *     if (player.hasPermission("vip")) {
 *         ItemStack(Material.DIAMOND)
 *     } else {
 *         ItemStack(Material.COAL)
 *     }
 * }
 * ```
 *
 * @param itemProvider Function that takes a player and returns the ItemStack to display
 * @return A Button with dynamic item display
 */
fun dynamicItem(itemProvider: (Player) -> ItemStack): Button {
    return buildButton {
        item(itemProvider)
    }
}

/**
 * Creates a simple button from a Material with an optional display name.
 *
 * Example:
 * ```kotlin
 * val button = Material.BARRIER.asButton("§cClose")
 * ```
 *
 * @param displayName Optional display name for the item
 * @return A Button displaying this material
 */
fun Material.asButton(displayName: String? = null): Button {
    val item = ItemStack(this).apply {
        if (displayName != null) {
            itemMeta = itemMeta?.apply {
                setDisplayName(displayName)
            }
        }
    }
    return Button.of(item)
}

/**
 * Creates a clickable button from a Material with a display name and click handler.
 *
 * Example:
 * ```kotlin
 * val button = Material.BARRIER.asButton("§cClose") { player ->
 *     player.closeInventory()
 * }
 * ```
 *
 * @param displayName Display name for the item
 * @param onClick Click handler that receives the player
 * @return A Button with the click handler
 */
fun Material.asButton(displayName: String, onClick: (Player) -> Unit): Button {
    val item = ItemStack(this).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(displayName)
        }
    }
    return item.onPlayerClick(Result.CANCEL_UPDATE, onClick)
}

/**
 * Creates a button with lore (description lines).
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withLore(
 *     "§7This is a diamond",
 *     "§7Click to get one!"
 * )
 * ```
 *
 * @param lines Lore lines to add to the item
 * @return A Button with the lore set
 */
fun ItemStack.withLore(vararg lines: String): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            lore = lines.toList()
        }
    }
    return Button.of(item)
}

/**
 * Creates a button with a display name.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withName("§bSpecial Diamond")
 * ```
 *
 * @param name The display name to set
 * @return A Button with the name set
 */
fun ItemStack.withName(name: String): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
        }
    }
    return Button.of(item)
}

/**
 * Creates a button with both display name and lore.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withNameAndLore(
 *     "§bSpecial Diamond",
 *     "§7Very shiny!",
 *     "§7Click to collect"
 * )
 * ```
 *
 * @param name The display name
 * @param lines Lore lines
 * @return A Button with name and lore set
 */
fun ItemStack.withNameAndLore(name: String, vararg lines: String): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
            lore = lines.toList()
        }
    }
    return Button.of(item)
}

/**
 * Creates an empty/invisible button using AIR material.
 *
 * Useful for creating space in GUIs.
 */
val EMPTY_BUTTON: Button = Button.of(ItemStack(Material.AIR))

/**
 * Creates a button that acts as a spacer (using a transparent glass pane).
 *
 * @param displayName Optional display name (defaults to empty string)
 */
fun spacerButton(displayName: String = " "): Button {
    return ItemStack(Material.GLASS_PANE).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(displayName)
        }
    }.asButton
}