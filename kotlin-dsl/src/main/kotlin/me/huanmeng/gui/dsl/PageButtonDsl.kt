@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.button.function.page.PlayerClickPageButtonInterface
import me.huanmeng.gui.gui.enums.Result
import me.huanmeng.gui.gui.impl.AbstractGuiPage
import me.huanmeng.gui.gui.impl.GuiPage
import me.huanmeng.gui.gui.impl.page.PageArea
import me.huanmeng.gui.gui.impl.page.PageButton
import me.huanmeng.gui.gui.impl.page.PageButton.Builder
import me.huanmeng.gui.gui.impl.page.PageButtonType
import org.bukkit.entity.Player

/**
 * Builds a [PageButton] for a GuiPage using a DSL configuration block.
 *
 * PageButtons are special buttons that handle page navigation (next, previous, first, last, etc.).
 * They automatically update the page and refresh the GUI display.
 *
 * Example:
 * ```kotlin
 * gui.buildPageButton {
 *     types(PageButtonTypes.NEXT)
 *     setButton { item = ItemStack(Material.ARROW) }
 *     handleClick { player, gui, pageArea, buttonType, clickData ->
 *         player.sendMessage("Going to next page!")
 *     }
 * }
 * ```
 *
 * @param lambda The DSL configuration block for the page button
 * @return A configured PageButton instance
 * @see PageButton
 */
fun GuiPage.buildPageButton(lambda: Builder.() -> Unit): PageButton {
    return PageButton.builder(this).apply(lambda).build()
}

/**
 * Sets the display button for this PageButton using a DSL block.
 *
 * This configures what the page navigation button looks like and how it behaves when clicked.
 *
 * @param lambda DSL block for configuring the button display
 * @see ButtonDsl
 */
fun Builder.setButton(lambda: ButtonDsl.() -> Unit) {
    button(buildButton(lambda))
}

/**
 * Sets a click handler for the page button with full access to click details.
 *
 * The handler receives the GUI, page area, button type, and click data.
 * It should return a Result indicating how the event should be handled.
 *
 * @param lambda The click handler function
 * @return Result indicating event handling behavior
 * @see Result
 */
fun Builder.click(
    lambda: (
        gui: AbstractGuiPage<*>, pageArea: PageArea, buttonType: PageButtonType, clickData: ClickData
    ) -> Result
) {
    click(PlayerClickPageButtonInterface(lambda))
}

/**
 * Sets a simplified click handler that executes an action without returning a result.
 *
 * The handler receives detailed information about the click context.
 * The event will automatically be cancelled and all GUI elements will update (CANCEL_UPDATE_ALL).
 *
 * @param lambda The handler function receiving player, GUI, page area, button type, and click data
 */
fun Builder.handleClick(lambda: (player: Player, gui: AbstractGuiPage<*>, pageArea: PageArea, buttonType: PageButtonType, clickData: ClickData) -> Unit) {
    click(PageButtonClickDsl().apply { handleClick = lambda })
}

/**
 * Configures the click behavior for this page button using a DSL block.
 *
 * Provides access to a DSL for defining click handling logic.
 *
 * @param lambda DSL block for configuring click behavior
 * @see PageButtonClickDsl
 */
fun Builder.onClick(lambda: PageButtonClickDsl.() -> Unit) {
    click(PageButtonClickDsl().apply(lambda))
}

/**
 * DSL class for configuring page button click handlers.
 *
 * This class simplifies creating click handlers for page buttons by providing
 * a cleaner API that automatically handles result types.
 */
class PageButtonClickDsl : PlayerClickPageButtonInterface {
    /**
     * The click handler function that will be invoked when the button is clicked.
     * Receives player, GUI, page area, button type, and full click data.
     */
    var handleClick: ((player: Player, gui: AbstractGuiPage<*>, pageArea: PageArea, buttonType: PageButtonType, clickData: ClickData) -> Unit)? =
        null

    override fun onClick(
        gui: AbstractGuiPage<*>,
        pageArea: PageArea,
        buttonType: PageButtonType,
        clickData: ClickData
    ): Result {
        handleClick?.invoke(clickData.player, gui, pageArea, buttonType, clickData)
        return Result.CANCEL_UPDATE_ALL
    }
}