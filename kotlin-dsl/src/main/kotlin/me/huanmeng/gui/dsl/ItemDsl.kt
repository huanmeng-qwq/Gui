package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.button.function.PlayerClickInterface
import me.huanmeng.gui.gui.enums.Result
import org.bukkit.inventory.ItemStack

fun ItemStack.onClick(result: Result = Result.CANCEL, block: (ClickData.() -> Unit)): Button {
    return buildButton {
        item = this@onClick
        click = PlayerClickInterface {
            block.invoke(it)
            return@PlayerClickInterface result
        }
    }
}