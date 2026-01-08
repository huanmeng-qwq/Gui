@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.impl.page.PageSetting
import me.huanmeng.gui.impl.page.PageSetting.Builder

/**
 * Builds a [PageSetting] using a DSL configuration block.
 *
 * PageSettings control the appearance and behavior of navigation buttons in paginated GUIs.
 * This includes buttons for next page, previous page, first page, last page, etc.
 *
 * Example:
 * ```kotlin
 * val settings = buildPageSetting {
 *     button {
 *         types(PageButtonTypes.NEXT)
 *         setButton { item = ItemStack(Material.ARROW) }
 *     }
 * }
 * ```
 *
 * @param lambda The DSL configuration block for the page setting
 * @return A configured PageSetting instance
 * @see PageSetting
 * @see PageSetting.Builder
 */
fun buildPageSetting(lambda: Builder.() -> Unit): PageSetting {
    return Builder().apply(lambda).build()
}
