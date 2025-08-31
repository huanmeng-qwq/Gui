@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.function.PlayerClickInterface
import me.huanmeng.gui.gui.button.function.PlayerItemInterface
import me.huanmeng.gui.gui.enums.Result
import me.huanmeng.gui.gui.slot.Slot
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 构建[Button]
 */
fun buildButton(lambda: ButtonDsl.() -> Unit): Button {
    val buttonDsl = ButtonDsl()
    lambda(buttonDsl)
    return Button.of(buttonDsl.showingItem, buttonDsl.click)
}

/**
 * 构建[Button]列表
 */
fun buildButtons(lambda: ButtonList.() -> Unit): List<Button> {
    return ButtonList().apply(lambda)
}

class ButtonDsl {
    var showingItem: PlayerItemInterface? = null
    var click: PlayerClickInterface? = null

    var item: ItemStack? = null
        set(value) {
            showingItem = PlayerItemInterface { value }
            field = value
        }

    /**
     * 设置按钮展示的物品
     */
    fun item(lambda: (player: Player) -> ItemStack) {
        showingItem = PlayerItemInterface(lambda)
    }

    /**
     * 处理点击并cancel事件
     * @see me.huanmeng.gui.gui.enums.Result.CANCEL
     */
    fun cancelClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL, lambda)
    }

    /**
     * 处理点击并cancel事件且更新当前按钮所在[Slot]的物品
     * @see me.huanmeng.gui.gui.enums.Result.CANCEL_UPDATE
     */
    fun updateClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL_UPDATE, lambda)
    }

    /**
     * 处理点击并cancel事件且更新当前Gui所有[Slot]的物品
     * @see me.huanmeng.gui.gui.enums.Result.CANCEL_UPDATE_ALL
     */
    fun updateAllClick(lambda: (player: Player) -> Unit) {
        click = PlayerClickInterface.handlePlayerClick(Result.CANCEL_UPDATE_ALL, lambda)
    }
}

class ButtonList(list: List<Button>) : ArrayList<Button>(list) {
    constructor() : this(arrayListOf())

    /**
     * 添加按钮
     */
    fun add(lambda: () -> Button) = add(lambda())

    /**
     * 添加按钮
     */
    fun button(lambda: ButtonDsl.() -> Unit) = add(buildButton(lambda))
}