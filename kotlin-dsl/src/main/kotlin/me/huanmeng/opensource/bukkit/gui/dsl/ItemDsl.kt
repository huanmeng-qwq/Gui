package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.button.ClickData
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickInterface
import me.huanmeng.opensource.bukkit.gui.enums.Result
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