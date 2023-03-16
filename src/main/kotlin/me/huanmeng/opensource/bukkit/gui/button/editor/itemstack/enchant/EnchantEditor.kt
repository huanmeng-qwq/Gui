package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.enchant

import me.huanmeng.limemc.bukkit.util.item.ItemUtil.getEnchLv
import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.HGui
import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.button.ClickButton
import me.huanmeng.opensource.bukkit.gui.button.EmptyButton
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.ItemStackCallback
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickCancelUpdateAllInterface
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

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
class EnchantEditor(player: Player, private val itemBuilder: ItemBuilder, private val enchantments: List<Enchantment>) :
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
                for (enchantment in enchantments) {
                    add(
                        ClickButton(
                            {
                                val enchLv = itemBuilder.getEnchLv(enchantment)
                                ItemBuilder(Material.BOOK).addUnsafeEnchantment(
                                    enchantment,
                                    1
                                ).apply {
                                    addLore("§b当前等级: $enchLv")
                                    line()
                                    addLore("§7左键+1级")
                                    addLore("§7右键-1级")
                                }.build()
                            },
                            PlayerClickCancelUpdateAllInterface { _, click, _, _, _, _ ->
                                val enchLv = itemBuilder.getEnchLv(enchantment)
                                if (click.isLeftClick) {
                                    itemBuilder.addUnsafeEnchantment(enchantment, enchLv + 1)
                                } else if (click.isRightClick) {
                                    val newLv = enchLv - 1
                                    if (newLv <= 0) {
                                        itemBuilder.removeEnchantment(enchantment)
                                    } else {
                                        itemBuilder.addUnsafeEnchantment(enchantment, newLv)
                                    }
                                }
                            })
                    )
                }
            }, 14, Slots.pattern(pattern, 'b')
        )
        gui.line(4)
        gui.title("编辑附魔")
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
        return gui
    }
}