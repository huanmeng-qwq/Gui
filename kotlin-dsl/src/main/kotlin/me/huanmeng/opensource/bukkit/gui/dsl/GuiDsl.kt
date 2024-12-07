@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.GuiButton
import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.draw.GuiDraw
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButton
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButtonTypes
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSettings
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import org.bukkit.entity.Player
import kotlin.math.min

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
fun <G : AbstractGui<G>> G.draw(lambda: GuiDraw<G>.() -> Unit) = lambda(draw())

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

/**
 * 使用预设的[PageSetting]
 * @see PageSettings.normal
 */
fun GuiPage.simplePageSetting() {
    pageSetting(PageSettings.normal(this))
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

/* Pattern to Gui*/
object Guis

open class PatternCustomGuiDsl(open val gui: GuiCustom, open val pattern: Array<out String>) {
    infix fun String.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, *toCharArray()), button)
        }
    }

    infix fun Array<out String>.map(button: Button) {
        val chatArray = this@map.flatMap { it.toCharArray().toList() }.toCharArray()
        chatArray.map(button)
    }

    infix fun Char.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, this@map), button)
        }
    }

    infix fun Array<Char>.map(button: Button) {
        this@map.toCharArray().map(button)
    }

    infix fun CharArray.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, *this@map), button)
        }
    }


    infix fun List<String>.mapString(button: Button) {
        forEach {
            it.map(button)
        }
    }

    infix fun List<Char>.mapChar(button: Button) {
        forEach {
            it.map(button)
        }
    }


    fun gui(lambda: GuiCustom.() -> Unit) {
        gui.lambda()
    }
}

class PatternPageGuiDsl(override val gui: GuiPage, override val pattern: Array<out String>) :
    PatternCustomGuiDsl(gui, pattern) {

    fun page(lambda: GuiPage.() -> Unit) {
        gui.lambda()
    }

    infix fun PageSetting.next(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.NEXT).build() })
    }

    infix fun PageSetting.prev(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.PREVIOUS).build() })
    }

    infix fun PageSetting.last(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.LAST).build() })
    }

    infix fun PageSetting.first(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.FIRST).build() })
    }
}

fun Guis.of(vararg patterns: String, lambda: PatternCustomGuiDsl.() -> Unit): GuiCustom {
    return GuiCustom().apply {
        line(min(9, patterns.size))

    }.also {
        PatternCustomGuiDsl(it, patterns).lambda()
    }
}

fun Guis.ofPage(vararg patterns: String, lambda: PatternPageGuiDsl.() -> Unit): GuiPage {
    return GuiPage().apply {
        line(min(9, patterns.size))
    }.also {
        PatternPageGuiDsl(it, patterns).lambda()
    }.apply {
        // check is set?
        requireNotNull(elementSlots) {
            "elementSlots is null"
        }
        requireNotNull(allItems) {
            "allItems is null"
        }
    }
}