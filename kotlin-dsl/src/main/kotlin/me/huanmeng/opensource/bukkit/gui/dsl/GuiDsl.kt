@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.GuiButton
import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.draw.GuiDraw
import me.huanmeng.opensource.bukkit.gui.impl.AbstractGuiCustom
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
fun Player.openGui(lambda: AbstractGuiCustom<*>.() -> Unit) = openGui(buildGui(lambda))

/**
 * 打开一个[GuiPage]
 */
fun Player.openPagedGui(lambda: GuiPage.() -> Unit) = openGui(buildPagedGui(lambda))
/* end */

/* GuiCustom method */
/**
 * 添加按钮, 自动取index为slot
 */
fun AbstractGuiCustom<*>.buttons(lambda: ButtonList.() -> Unit) {
    val arrayList = ButtonList()
    lambda(arrayList)
    arrayList.forEachIndexed { index, btn ->
        addAttachedButton(GuiButton(buildSlot(index), btn))
    }
}

/**
 * 绘制Gui
 */
fun <G : AbstractGuiCustom<G>> G.draw(lambda: GuiDraw<G>.() -> Unit) = lambda(draw())
fun <G : AbstractGui<G>> G.draw(lambda: GuiDraw<G>.() -> Unit) = lambda(draw())

/**
 * 给一个玩家打开当前Gui
 */
fun AbstractGuiCustom<*>.openGui(player: Player) {
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
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slot: Slot, lambda: ButtonDsl.() -> Unit) {
    set(slot, buildButton(lambda))
}

/**
 * 设置按钮
 */
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slots: Slots, lambda: ButtonDsl.() -> Unit) {
    set(slots, buildButton(lambda))
}

/**
 * 设置按钮
 */
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slots: Slots, lambda: ButtonList.() -> Unit) =
    set(slots, ButtonList().apply(lambda))

class SetButton<G : AbstractGui<G>>(private val draw: GuiDraw<G>, private val slot: Slot) {
    infix fun button(button: Button) {
        draw.set(slot, button)
    }
}

class SetButtons<G : AbstractGui<G>>(private val draw: GuiDraw<G>, private val slots: Slots) {
    infix fun button(button: Button) {
        draw.set(slots, button)
    }

    infix fun buttons(lambda: ButtonList.() -> Unit) {
        draw.set(slots, ButtonList().apply(lambda))
    }
}

infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Int): SetButton<G> {
    return SetButton(this, Slot.of(slot))
}

infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Slot): SetButton<G> {
    return SetButton(this, slot)
}

infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Pair<Int, Int>): SetButton<G> {
    return SetButton(this, Slot.ofBukkit(slot.first, slot.second))
}

infix fun <G : AbstractGui<G>> GuiDraw<G>.gameSlot(slot: Pair<Int, Int>): SetButton<G> {
    return SetButton(this, Slot.ofGame(slot.first, slot.second))
}

infix fun <G : AbstractGui<G>> GuiDraw<G>.slots(slots: Slots): SetButtons<G> {
    return SetButtons(this, slots)
}
/* end */

/* Pattern to Gui*/
object Guis

open class PatternCustomGuiDsl<G : AbstractGuiCustom<G>>(open val gui: G, open val pattern: Array<out String>) {
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


    fun gui(lambda: AbstractGuiCustom<*>.() -> Unit) {
        gui.lambda()
    }
}

class PatternPageGuiDsl(override val gui: GuiPage, override val pattern: Array<out String>) :
    PatternCustomGuiDsl<GuiPage>(gui, pattern) {

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

fun Guis.of(vararg patterns: String, lambda: PatternCustomGuiDsl<GuiCustom>.() -> Unit): GuiCustom {
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