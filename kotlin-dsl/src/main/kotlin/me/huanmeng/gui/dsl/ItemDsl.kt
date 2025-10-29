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
 * making it easier to write simple click handlers. Supports all Result types.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).onPlayerClick { player ->
 *     player.sendMessage("You clicked a diamond!")
 * }
 *
 * // With ALLOW result (won't cancel the event)
 * val allowButton = ItemStack(Material.CHEST).onPlayerClick(Result.ALLOW) { player ->
 *     player.sendMessage("Opening chest...")
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
        click = PlayerClickInterface { clickData ->
            block(clickData.player)
            result
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
 * @param itemProvider Function that takes a player and returns the ItemStack to display (can be null)
 * @return A Button with dynamic item display
 */
fun dynamicItem(itemProvider: (Player) -> ItemStack?): Button {
    return buildButton {
        item(itemProvider)
    }
}

/**
 * Creates a button with dynamic item display and additional ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val button = dynamicItem(
 *     itemProvider = { player ->
 *         if (player.hasPermission("vip")) {
 *             ItemStack(Material.DIAMOND).apply {
 *                 itemMeta = itemMeta?.apply {
 *                     setDisplayName("§b${player.name}'s VIP Diamond")
 *                 }
 *             }
 *         } else {
 *             ItemStack(Material.COAL)
 *         }
 *     }
 * ) {
 *     updateClick { player ->
 *         player.sendMessage("§aClicked dynamic item!")
 *     }
 * }
 * ```
 *
 * @param itemProvider Function that takes a player and returns the ItemStack to display (can be null)
 * @param block Configuration block for the button
 * @return A configured Button with dynamic item display
 */
fun dynamicItem(itemProvider: (Player) -> ItemStack?, block: ButtonDsl.() -> Unit): Button {
    return buildButton {
        item(itemProvider)
        block()
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
 * @param result The click result behavior (defaults to CANCEL_UPDATE)
 * @param onClick Click handler that receives the player
 * @return A Button with the click handler
 */
fun Material.asButton(displayName: String, result: Result = Result.CANCEL_UPDATE, onClick: (Player) -> Unit): Button {
    val item = ItemStack(this).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(displayName)
        }
    }
    return item.onPlayerClick(result, onClick)
}

/**
 * Creates a button from a Material with full ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val button = Material.DIAMOND.asButton {
 *     updateClick { player ->
 *         player.sendMessage("§bClicked!")
 *     }
 * }
 * ```
 *
 * @param block Configuration block for the button
 * @return A configured Button
 */
fun Material.asButton(block: ButtonDsl.() -> Unit): Button {
    return ItemStack(this).asButton(block)
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
 * Creates a button with lore and additional ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withLore(
 *     "§7This is a diamond",
 *     "§7Click to get one!"
 * ) {
 *     updateClick { player ->
 *         player.inventory.addItem(ItemStack(Material.DIAMOND))
 *     }
 * }
 * ```
 *
 * @param lines Lore lines to add to the item
 * @param block Configuration block for the button
 * @return A configured Button with the lore set
 */
fun ItemStack.withLore(vararg lines: String, block: ButtonDsl.() -> Unit): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            lore = lines.toList()
        }
    }
    return buildButton {
        this.item = item
        block()
    }
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
 * Creates a button with a display name and additional ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withName("§bSpecial Diamond") {
 *     updateClick { player ->
 *         player.sendMessage("Clicked!")
 *     }
 * }
 * ```
 *
 * @param name The display name to set
 * @param block Configuration block for the button
 * @return A configured Button with the name set
 */
fun ItemStack.withName(name: String, block: ButtonDsl.() -> Unit): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
        }
    }
    return buildButton {
        this.item = item
        block()
    }
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
 * Creates a button with display name, lore, and additional ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.DIAMOND).withNameAndLore(
 *     "§bSpecial Diamond",
 *     "§7Very shiny!",
 *     "§7Click to collect"
 * ) {
 *     updateClick { player ->
 *         player.sendMessage("§aCollected!")
 *     }
 * }
 * ```
 *
 * @param name The display name
 * @param lines Lore lines
 * @param block Configuration block for the button
 * @return A configured Button with name and lore set
 */
fun ItemStack.withNameAndLore(name: String, vararg lines: String, block: ButtonDsl.() -> Unit): Button {
    val item = this.clone().apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
            lore = lines.toList()
        }
    }
    return buildButton {
        this.item = item
        block()
    }
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

/**
 * Creates a spacer button with ButtonDsl configuration.
 *
 * Example:
 * ```kotlin
 * val spacer = spacerButton("§7---") {
 *     cancelClick { player ->
 *         // Do nothing, acts as separator
 *     }
 * }
 * ```
 *
 * @param displayName Optional display name (defaults to empty string)
 * @param block Configuration block for the button
 * @return A configured spacer Button
 */
fun spacerButton(displayName: String = " ", block: ButtonDsl.() -> Unit): Button {
    val item = ItemStack(Material.GLASS_PANE).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(displayName)
        }
    }
    return buildButton {
        this.item = item
        block()
    }
}

/**
 * Creates a new button with the same item but overrides the click handler.
 *
 * This allows you to take an existing button and add/replace its click behavior
 * without modifying the original button. Supports all Result types.
 *
 * Example:
 * ```kotlin
 * val baseButton = ItemStack(Material.DIAMOND).asButton
 *
 * // Keep original result behavior
 * val clickableButton = baseButton.withClick { player ->
 *     player.sendMessage("Clicked!")
 * }
 *
 * // Override with specific result
 * val allowButton = baseButton.withClick(Result.ALLOW) { player ->
 *     player.sendMessage("Allowed!")
 * }
 * ```
 *
 * @param result The click result behavior (null = keep original button's result)
 * @param block The new click handler
 * @return A new Button with the overridden click handler
 */
fun Button.withClick(result: Result? = null, block: (Player) -> Unit): Button {
    val originalButton = this
    return buildButton {
        item { player -> originalButton.getShowItem(player) }
        click = PlayerClickInterface { clickData ->
            block(clickData.player)
            result ?: originalButton.onClick(clickData)
        }
    }
}

/**
 * Creates a new button with the same item but adds an additional click handler.
 *
 * Note: This only executes the new handler. Use withClick() to completely replace the handler.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.EMERALD).asButton
 * val enhancedButton = button.andThen { player ->
 *     player.sendMessage("Additional action!")
 * }
 * ```
 *
 * @param block The click handler to execute
 * @return A new Button with the click handler
 */
fun Button.andThen(block: (Player) -> Unit): Button {
    return withClick(Result.CANCEL_UPDATE, block)
}

/**
 * Creates a new button with the same item but with a ClickData-based click handler.
 *
 * This gives you full access to the ClickData object including event details.
 *
 * Example:
 * ```kotlin
 * val button = ItemStack(Material.GOLD_INGOT).asButton
 * val advancedButton = button.withClickData { clickData ->
 *     val player = clickData.player
 *     val clickType = clickData.event?.click
 *     player.sendMessage("Clicked with: $clickType")
 * }
 * ```
 *
 * @param result The click result behavior (default: CANCEL_UPDATE)
 * @param block The click handler that receives ClickData
 * @return A new Button with the click handler
 */
fun Button.withClickData(result: Result = Result.CANCEL_UPDATE, block: (ClickData) -> Unit): Button {
    return buildButton {
        item { player -> this@withClickData.getShowItem(player) }
        click = PlayerClickInterface { clickData ->
            block(clickData)
            result
        }
    }
}

/**
 * Disables click interactions for this button.
 *
 * Creates a new button that looks the same but doesn't respond to clicks.
 *
 * Example:
 * ```kotlin
 * val displayOnlyButton = myButton.withoutClick()
 * ```
 *
 * @return A new Button with no click handler
 */
fun Button.withoutClick(): Button {
    return buildButton {
        item { player -> this@withoutClick.getShowItem(player) }
        cancelClick { /* Do nothing */ }
    }
}