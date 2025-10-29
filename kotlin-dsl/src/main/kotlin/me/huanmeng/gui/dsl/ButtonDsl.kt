@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.button.function.PlayerClickInterface
import me.huanmeng.gui.gui.button.function.PlayerItemInterface
import me.huanmeng.gui.gui.enums.Result
import me.huanmeng.gui.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Builds a [Button] using a DSL configuration block.
 *
 * This function provides a Kotlin DSL for creating buttons with custom items and click handlers.
 * The button can be configured with display items, click actions, and result behavior.
 *
 * @param lambda The DSL configuration block for the button
 * @return A configured Button instance
 * @see ButtonDsl
 */
fun buildButton(lambda: ButtonDsl.() -> Unit): Button {
    val buttonDsl = ButtonDsl()
    lambda(buttonDsl)
    return Button.of(buttonDsl.showingItem, buttonDsl.click)
}

/**
 * Builds a list of [Button] instances using a DSL configuration block.
 *
 * Useful for creating multiple buttons at once with a builder pattern.
 *
 * @param lambda The DSL configuration block for the button list
 * @return A list of configured Button instances
 * @see ButtonList
 */
fun buildButtons(lambda: ButtonList.() -> Unit): List<Button> {
    return ButtonList().apply(lambda)
}

/**
 * Extension property to convert an [ItemStack] to a display-only [Button].
 *
 * The button will show this item but have no click interaction.
 */
val ItemStack.asButton: Button
    get() = Button.of(this)

/**
 * Extension function to convert an [ItemStack] to a [Button] with custom configuration.
 *
 * Allows adding click handlers and other button behavior to an ItemStack.
 *
 * @param block The configuration block for the button
 * @return A configured Button displaying this ItemStack
 */
fun ItemStack.asButton(block: (ButtonDsl) -> Unit): Button {
    return buildButton {
        item = this@asButton
        block(this@buildButton)
    }
}

/**
 * DSL builder class for configuring [Button] instances.
 *
 * This class provides a fluent API for creating buttons with:
 * - Display items (static or player-dependent)
 * - Click handlers with various result behaviors
 * - Automatic GUI updates on interaction
 *
 * Example usage:
 * ```kotlin
 * buildButton {
 *     item = ItemStack(Material.DIAMOND)
 *     updateClick { player ->
 *         player.sendMessage("Clicked!")
 *     }
 * }
 * ```
 */
class ButtonDsl {
    /**
     * The item supplier that determines what ItemStack to display.
     * Can be set directly or via [item] property/function.
     */
    var showingItem: PlayerItemInterface? = null

    /**
     * The click handler for this button.
     * Configure via helper functions like [cancelClick], [updateClick], or [clickHandler].
     */
    var click: PlayerClickInterface? = null

    /**
     * The result behavior when using [clickHandler].
     * Must be set via [canceling], [updating], or [updatingAll] before calling [clickHandler].
     */
    var result: Result? = null

    /**
     * Sets a static ItemStack to display in the button.
     * The item will be the same for all players.
     *
     * @property value The ItemStack to display, or null for no item
     */
    var item: ItemStack? = null
        set(value) {
            showingItem = PlayerItemInterface { value }
            field = value
        }

    /**
     * Sets a player-dependent item supplier for the button.
     * The ItemStack can vary based on the viewing player.
     *
     * @param lambda Function that takes a player and returns the ItemStack to display (can be null)
     */
    fun item(lambda: (player: Player) -> ItemStack?) {
        showingItem = PlayerItemInterface(lambda)
    }

    /**
     * Sets a click handler that cancels the click event and prevents default behavior.
     * The inventory interaction will be cancelled, but the GUI won't update.
     *
     * @param lambda The handler function that receives the clicking player
     * @see Result.CANCEL
     */
    fun cancelClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL, lambda)
    }

    /**
     * Sets a click handler that cancels the event and updates this button's slot.
     * After the handler runs, only this button will refresh its display.
     *
     * @param lambda The handler function that receives the clicking player
     * @see Result.CANCEL_UPDATE
     */
    fun updateClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL_UPDATE, lambda)
    }

    /**
     * Sets a click handler that cancels the event and updates all slots in the GUI.
     * After the handler runs, all buttons in the GUI will refresh their displays.
     *
     * @param lambda The handler function that receives the clicking player
     * @see Result.CANCEL_UPDATE_ALL
     */
    fun updateAllClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL_UPDATE_ALL, lambda)
    }

    /**
     * Sets the result to CANCEL for use with [clickHandler].
     * The click will be cancelled but no updates will occur.
     */
    fun canceling() {
        result = Result.CANCEL
    }

    /**
     * Sets the result to CANCEL_UPDATE for use with [clickHandler].
     * The click will be cancelled and this button's slot will update.
     */
    fun updating() {
        result = Result.CANCEL_UPDATE
    }

    /**
     * Sets the result to CANCEL_UPDATE_ALL for use with [clickHandler].
     * The click will be cancelled and all GUI slots will update.
     */
    fun updatingAll() {
        result = Result.CANCEL_UPDATE_ALL
    }

    /**
     * Sets a low-level click handler with full access to click data.
     * Requires calling [canceling], [updating], or [updatingAll] first.
     *
     * This provides more control than the simple click handlers, giving access to
     * click type, slot position, and other event details via [ClickData].
     *
     * @param block The handler function that receives click data
     * @throws IllegalArgumentException if result is not set before calling this
     */
    fun clickHandler(block: (ClickData) -> Unit) {
        click = PlayerClickInterface.of(result ?: throw IllegalArgumentException("Result must be set before clickHandler. Call canceling(), updating(), or updatingAll() first."), block)
    }
}

/**
 * A mutable list of [Button] instances with DSL builder methods.
 *
 * Provides convenient methods for adding buttons to a list using builder patterns.
 *
 * @param list Initial list of buttons (defaults to empty list)
 */
class ButtonList(list: List<Button>) : ArrayList<Button>(list) {
    constructor() : this(arrayListOf())

    /**
     * Adds a button by evaluating a lambda that returns a Button.
     *
     * @param lambda Function that creates and returns a Button
     * @return true if the button was added successfully
     */
    fun add(lambda: () -> Button) = add(lambda())

    /**
     * Adds a button using a DSL configuration block.
     *
     * @param lambda DSL block for configuring the button
     * @return true if the button was added successfully
     * @see buildButton
     */
    fun button(lambda: ButtonDsl.() -> Unit) = add(buildButton(lambda))
}