@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.GuiButton
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerItemInterface
import me.huanmeng.opensource.bukkit.gui.draw.GuiDraw
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun buildGui(lambda: GuiCustom.() -> Unit): GuiCustom {
    return GuiCustom().apply(lambda)
}

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
fun Player.openGui(gui: AbstractGui<*>) {
    gui.apply { player = this@openGui }.openGui()
}

fun Player.openGui(lambda: GuiCustom.() -> Unit) = openGui(buildGui(lambda))

fun Player.openPagedGui(lambda: GuiPage.() -> Unit) = openGui(buildPagedGui(lambda))
/* end */

/* GuiCustom method */
fun GuiCustom.buttons(lambda: ButtonList.() -> Unit) {
    val arrayList = ButtonList()
    lambda(arrayList)
    arrayList.forEachIndexed { index, btn ->
        addAttachedButton(GuiButton(buildSlot(index), btn))
    }
}

fun GuiCustom.draw(lambda: GuiDraw<GuiCustom>.() -> Unit) = lambda(draw())

fun GuiCustom.openGui(player: Player) {
    this.player = player
    openGui()
}
/* end */

/* GuiPage method */
fun GuiPage.pageSetting(lambda: () -> PageSetting) {
    pageSetting(lambda())
}
/* end */

/* GuiDraw method*/
fun GuiDraw<out GuiCustom>.setButton(slot: Slot, lambda: ButtonDsl.() -> Unit) {
    set(slot, buildButton(lambda))
}

fun GuiDraw<out GuiCustom>.setButton(slots: Slots, lambda: ButtonDsl.() -> Unit) {
    set(slots, buildButton(lambda))
}

fun GuiDraw<out GuiCustom>.setButton(slots: Slots, lambda: ButtonList.() -> Unit) =
    set(slots, ButtonList().apply(lambda))
/* end */

private class GuiDslTest {
    fun example() {
        buildGui {
            buttons {

            }
            draw {
                setButton(Slot.of(1)) {
                    var a = 1
                    showingItem = PlayerItemInterface {
                        ItemStack(Material.values()[a++])
                    }
                    updateClick {
                        it.inventory.addItem(showingItem!!.get(it))
                    }
                }
            }
        }

        buildPagedGui {
            allItems = buildButtons {
                for (i in 0..60) {
                    button {
                        showingItem = buildButtonItem(ItemStack(Material.values()[i]))
                    }
                }
            }
            elementSlots = buildSlotsByLine { line ->
                return@buildSlotsByLine buildList {
                    for (i in 0..9 * line) {
                        add(buildSlot(i))
                    }
                }
            }
        }
    }
}