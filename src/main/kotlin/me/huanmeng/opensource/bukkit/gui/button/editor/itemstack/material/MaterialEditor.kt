package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.material

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
class MaterialEditor(player: Player, private val itemBuilder: ItemBuilder, private val materials: List<Material>) :
    HGui(player) {
    val pattern: Array<String> = arrayOf(
        "xxxxxxxxx",
        "xbbbbbbbx",
        "xbbbbbbbx",
        "xbbbbbbbx",
        "xbbbbbbbx",
        "xxxxkxxxx",
    )

    var callback: ItemStackCallback? = null

    override fun gui(): AbstractGui<in GuiCustom> {
        val gui = GuiPage(
            context.player, buildList {
                for (material in materials) {
                    if (material == Material.AIR) {
                        continue
                    }
                    add(
                        ClickButton(
                            {
                                ItemBuilder(material)
                                    .apply {
                                        if (itemBuilder.build().type == build().type) {
                                            addFlag(ItemFlag.HIDE_ENCHANTS)
                                            addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                                        }
                                        setName("§e点击选择")
                                    }.build()
                            },
                            PlayerSimpleCancelUpdateAllInterface {
                                itemBuilder.setType(material)
                                itemBuilder.setDurability(0)
                                callback?.call(itemBuilder.build())
                            })
                    )
                }
            }, 28, Slots.pattern(pattern, 'b')
        )
        gui.title("编辑物品类型")
        val emptyButton = EmptyButton {
            ItemBuilder(Material.GLASS, 1, 7).setName("").build()
        }
        gui.draw()[Slots.pattern(pattern, 'x')] = emptyButton
        gui.draw().set(Slot.ofGame(1, 6), Button.of({
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
        gui.draw().set(Slot.ofGame(9, 6), Button.of({
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
                        .addLore("§7点击返回")
                        .build()

                },
                    PlayerSimpleCancelInterface {
                        callback?.call(itemBuilder.build())
                    })
            } else {
                emptyButton
            }
        )
        gui.line(6)
        return gui
    }
}