@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting.Builder

/**
 * 构建[PageSetting]
 */
fun buildPageSetting(lambda: Builder.() -> Unit): PageSetting {
    return Builder().apply(lambda).build()
}
