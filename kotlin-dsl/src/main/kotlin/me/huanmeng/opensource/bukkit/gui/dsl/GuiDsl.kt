@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.GuiButton
import me.huanmeng.opensource.bukkit.gui.draw.GuiDraw
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import org.bukkit.entity.Player

/**
 * 构建一个[GuiCustom]
 */
fun buildGui(lambda: GuiCustom.() -> Unit): GuiCustom {
    return GuiCustom().apply(lambda)
}

/**
 * 构建一个[GuiPage]
 */
fun buildPagedGui(lambda: GuiPage.() -> Unit): GuiPage {
    return GuiPage().apply(lambda).apply {
        // check is set?
        requireNotNull(elementSlots) {
            "elementSlots is null"
        }
        requireNotNull(allItems) {
            "allItems is null"
        }
    }
}

/* Player method */
/**
 * 打开一个Gui
 */
fun Player.openGui(gui: AbstractGui<*>) {
    gui.apply { player = this@openGui }.openGui()
}

/**
 * 打开一个[GuiCustom]
 */
fun Player.openGui(lambda: GuiCustom.() -> Unit) = openGui(buildGui(lambda))

/**
 * 打开一个[GuiPage]
 */
fun Player.openPagedGui(lambda: GuiPage.() -> Unit) = openGui(buildPagedGui(lambda))
/* end */

/* GuiCustom method */
/**
 * 添加按钮, 自动取index为slot
 */
fun GuiCustom.buttons(lambda: ButtonList.() -> Unit) {
    val arrayList = ButtonList()
    lambda(arrayList)
    arrayList.forEachIndexed { index, btn ->
        addAttachedButton(GuiButton(buildSlot(index), btn))
    }
}

/**
 * 绘制Gui
 */
fun GuiCustom.draw(lambda: GuiDraw<GuiCustom>.() -> Unit) = lambda(draw())

/**
 * 给一个玩家打开当前Gui
 */
fun GuiCustom.openGui(player: Player) {
    this.player = player
    openGui()
}
/* end */

/* GuiPage method */
/**
 * 设置[GuiPage]的[PageSetting]
 */
fun GuiPage.pageSetting(lambda: () -> PageSetting) {
    pageSetting(lambda())
}
/* end */

/* GuiDraw method*/
/**
 * 设置按钮
 */
fun GuiDraw<out GuiCustom>.setButton(slot: Slot, lambda: ButtonDsl.() -> Unit) {
    set(slot, buildButton(lambda))
}

/**
 * 设置按钮
 */
fun GuiDraw<out GuiCustom>.setButton(slots: Slots, lambda: ButtonDsl.() -> Unit) {
    set(slots, buildButton(lambda))
}

/**
 * 设置按钮
 */
fun GuiDraw<out GuiCustom>.setButton(slots: Slots, lambda: ButtonList.() -> Unit) =
    set(slots, ButtonList().apply(lambda))
/* end */