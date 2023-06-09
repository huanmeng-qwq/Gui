@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.button.function.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun buildButton(lambda: ButtonDsl.() -> Unit): Button {
    val buttonDsl = ButtonDsl()
    lambda(buttonDsl)
    return Button.of(buttonDsl.showingItem, buttonDsl.click)
}

fun buildButtons(lambda: ButtonList.() -> Unit): List<Button> {
    return ButtonList().apply(lambda)
}

class ButtonDsl {
    var showingItem: PlayerItemInterface? = null
    var click: PlayerClickInterface? = null

    fun item(lambda: (player: Player) -> ItemStack) {
        showingItem = PlayerItemInterface(lambda)
    }

    fun cancelClick(lambda: (player: Player) -> Unit) {
        click = PlayerSimpleCancelInterface(lambda)
    }

    fun updateClick(lambda: (player: Player) -> Unit) {
        click = PlayerSimpleCancelUpdateInterface(lambda)
    }

    fun updateAllClick(lambda: (player: Player) -> Unit) {
        click = PlayerSimpleCancelUpdateAllInterface(lambda)
    }
}

class ButtonList(val list: MutableList<Button>) : MutableList<Button> by list {
    constructor() : this(ArrayList())

    fun add(lambda: () -> Button) = add(lambda())

    fun button(lambda: ButtonDsl.() -> Unit) = add(buildButton(lambda))
}