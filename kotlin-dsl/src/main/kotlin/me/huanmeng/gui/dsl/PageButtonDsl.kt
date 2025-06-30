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
 * 构建[PageButton]
 */
fun GuiPage.buildPageButton(lambda: Builder.() -> Unit): PageButton {
    return PageButton.builder(this).apply(lambda).build()
}

/**
 * 设置原始[Button]
 */
fun Builder.setButton(lambda: ButtonDsl.() -> Unit) {
    button(buildButton(lambda))
}

/**
 * 设置点击按钮后的触发操作
 */
fun Builder.click(
    lambda: (
        gui: AbstractGuiPage<*>, pageArea: PageArea, buttonType: PageButtonType, clickData: ClickData
    ) -> Result
) {
    click(PlayerClickPageButtonInterface(lambda))
}

/**
 * 处理点击后的操作
 */
fun Builder.handleClick(lambda: (player: Player, gui: AbstractGuiPage<*>, pageArea: PageArea, buttonType: PageButtonType, clickData: ClickData) -> Unit) {
    click(PageButtonClickDsl().apply { handleClick = lambda })
}

/**
 * 当点击时执行的操作
 */
fun Builder.onClick(lambda: PageButtonClickDsl.() -> Unit) {
    click(PageButtonClickDsl().apply(lambda))
}

class PageButtonClickDsl : PlayerClickPageButtonInterface {
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