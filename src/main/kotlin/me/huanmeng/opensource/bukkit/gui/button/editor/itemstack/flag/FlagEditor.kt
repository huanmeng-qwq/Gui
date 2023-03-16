package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.flag

import me.huanmeng.opensource.bukkit.util.item.ItemUtil.hasFlag
import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.HGui
import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.button.ClickButton
import me.huanmeng.opensource.bukkit.gui.button.EmptyButton
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.ItemStackCallback
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerSimpleCancelInterface
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerSimpleCancelUpdateAllInterface
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
class FlagEditor(player: Player, private val itemBuilder: ItemBuilder, private val flags: List<ItemFlag>) :
    HGui(player) {
    val pattern: Array<String> = arrayOf(
        "xxxxxxxxx",
        "xbbbbbbbx",
        "xbbbbbbbx",
        "xxxxkxxxx",
    )

    var callback: ItemStackCallback? = null

    override fun gui(): AbstractGui<in GuiCustom> {
        val gui = GuiPage(
            context.player, buildList {
                for (flag in flags) {
                    add(
                        ClickButton(
                            {
                                val hasFlag = itemBuilder.hasFlag(flag)
                                ItemBuilder(Material.PAPER)
                                    .apply {
                                        val color = if (hasFlag) "§a" else "§c"
                                        setName("$color${flag.name}")
                                        addLore("§7当前: $color$hasFlag")
                                        line()
                                        addLore("§b点击修改为 ${!hasFlag}")
                                        if (hasFlag) {
                                            addFlag(ItemFlag.HIDE_ENCHANTS)
                                            addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                                        }
                                    }.build()
                            },
                            PlayerSimpleCancelUpdateAllInterface {
                                val hasFlag = itemBuilder.hasFlag(flag)
                                if (hasFlag) {
                                    itemBuilder.removeFlag(flag)
                                } else {
                                    itemBuilder.addFlag(flag)
                                }
                            })
                    )
                }
            }, 14, Slots.pattern(pattern, 'b')
        )
        gui.title("编辑物品Flag")
        val emptyButton = EmptyButton {
            ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).setName("").build()
        }
        gui.draw()[Slots.pattern(pattern, 'x')] = emptyButton
        gui.draw().set(Slot.ofGame(1, 4), Button.of({
            if (gui.pagination().hasLast(gui.page())) {
                ItemBuilder(Material.ARROW).apply {
                    setName("§a上一页")
                }.build()
            } else {
                emptyButton.getShowItem(it)
            }
        }, PlayerSimpleCancelUpdateAllInterface {
            if (gui.pagination().hasLast(gui.page())) {
                gui.page(gui.page() - 1)
            }
        }))
        gui.draw().set(Slot.ofGame(9, 4), Button.of({
            if (gui.pagination().hasNext(gui.page())) {
                ItemBuilder(Material.ARROW).apply {
                    setName("§a下一页")
                }.build()
            } else {
                emptyButton.getShowItem(it)
            }
        }, PlayerSimpleCancelUpdateAllInterface {
            if (gui.pagination().hasNext(gui.page())) {
                gui.page(gui.page() + 1)

            }
        }))
        gui.draw()[Slots.pattern(pattern, 'k')] = listOf(
            if (callback != null) {
                ClickButton({
                    return@ClickButton ItemBuilder(Material.HOPPER).setName(callback!!.display())
                        .addLore("§7点击应用并返回")
                        .build()

                },
                    PlayerSimpleCancelInterface {
                        callback?.call(itemBuilder.build())
                    })
            } else {
                emptyButton
            }
        )
        gui.line(4)
        return gui
    }
}