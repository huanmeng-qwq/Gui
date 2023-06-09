@file:Suppress("unused")
package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.button.function.page.PlayerClickPageButtonInterface
import me.huanmeng.opensource.bukkit.gui.enums.Result
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButton
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButton.Builder
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButtonType
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryType

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
        gui: GuiPage, buttonType: PageButtonType, slot: Slot, player: Player, click: ClickType, action: InventoryAction,
        slotType: InventoryType.SlotType, slotKey: Int, hotBarKey: Int
    ) -> Result
) {
    click(PlayerClickPageButtonInterface(lambda))
}

/**
 * 处理点击后的操作
 */
fun Builder.handleClick(lambda: (player: Player, gui: GuiPage, buttonType: PageButtonType) -> Unit) {
    click(PageButtonClickDsl().apply { handleClick = lambda })
}

/**
 * 当点击时执行的操作
 */
fun Builder.onClick(lambda: PageButtonClickDsl.() -> Unit) {
    click(PageButtonClickDsl().apply(lambda))
}

class PageButtonClickDsl : PlayerClickPageButtonInterface {
    var handleClick: ((player: Player, gui: GuiPage, buttonType: PageButtonType) -> Unit)? = null
    override fun onClick(
        gui: GuiPage,
        buttonType: PageButtonType,
        slot: Slot,
        player: Player,
        click: ClickType,
        action: InventoryAction,
        slotType: InventoryType.SlotType,
        slotKey: Int,
        hotBarKey: Int
    ): Result {
        handleClick?.invoke(player, gui, buttonType)
        return Result.CANCEL_UPDATE_ALL
    }

}