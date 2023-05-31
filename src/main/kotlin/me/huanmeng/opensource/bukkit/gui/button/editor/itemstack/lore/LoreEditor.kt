package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.lore

import me.huanmeng.opensource.bukkit.util.item.ItemUtil.getLores
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
import org.bukkit.entity.Player
import kotlin.math.min

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
class LoreEditor(
    user: Player,
    private val itemBuilder: ItemBuilder,
    val edit: ((index: Int, loreEditor: LoreEditor, lores: MutableList<String>) -> Unit)?,
    val add: ((index: Int, loreEditor: LoreEditor, lores: MutableList<String>) -> Unit)?
) : HGui(user) {
    var callback: ItemStackCallback? = null


    override fun gui(): AbstractGui<in GuiCustom> {
        val list: MutableList<Button> = ArrayList()
        val editor = this
        var delMode = false

        val gui = GuiPage(
            context.player,
            list,
            21,
            Slots.PATTERN_LINE_DEFAULT
        )

        gui.title("编辑Lore 第${gui.page()}页")
        fun refresh() {
            list.clear()
            val lores = itemBuilder.build().getLores().toMutableList()
            for ((index, lore) in lores.withIndex()) {
                list.add(
                    Button.of({
                        ItemBuilder(Material.PAINTING, index + 1).apply {
                            setName(lore)
                            addLore("§8第${index + 1}行")
                            addLore("")
                            if (delMode) {
                                addLore("§c左键清空")
                                addLore("§c中键删除当前与后面所有的")
                                addLore("§c右键删除")
                            } else {
                                addLore("§e点击编辑")
                            }
                        }.build()
                    },
                        PlayerClickCancelUpdateAllInterface { player, click, _, _, _, _ ->
                            if (delMode) {
                                if (click.isLeftClick) {
                                    lores[index] = "§h§m"
                                } else if (click.isCreativeAction) {
                                    while (lores.size > index) {
                                        lores.removeAt(lores.size - 1)
                                    }
                                } else if (click.isRightClick) {
                                    lores.removeAt(index)
                                }
                                itemBuilder.setLore(lores)
                                refresh()
                                return@PlayerClickCancelUpdateAllInterface
                            }
                            gui.onClose()
                            add?.invoke(0, this, lores)
                            /*NmsHelper.get()
                                .createAnvil(player) { e ->
                                    if (e.slot != AnvilSlot.OUTPUT) {
                                        return@createAnvil
                                    }
                                    e.willDestroy = true
                                    lores[index] = e.name.color()
                                    itemBuilder.setLore(lores)
                                    editor.open()
                                }.apply {
                                    setSlot(
                                        AnvilSlot.INPUT_LEFT,
                                        ItemBuilder(Material.PAPER).setNameUnColor(lore.unColor().replace("&h&m", ""))
                                            .build()
                                    )
                                }.open()*/
                        }),
                )
            }
            while (!delMode && list.size < gui.elementsPerPage() * (gui.page() + 1)) {
                val index = list.size
                list.add(
                    Button.of(
                        {
                            ItemBuilder(Material.BUCKET).setName("§e点击编辑第${index + 1}行")
                                .setAmount(min(64, index + 1))
                                .build()
                        },
                        PlayerSimpleCancelUpdateAllInterface {
                            gui.onClose()
                            edit?.invoke(index, this, lores)
                            /*NmsHelper.get().createAnvil(it) { e ->
                                if (e.slot != AnvilSlot.OUTPUT) {
                                    return@createAnvil
                                }
                                e.willDestroy = true
                                while (lores.size <= index) {
                                    lores.add("§h§m")
                                }
                                lores[index] = e.name.color()
                                itemBuilder.setLore(lores)
                                editor.open()
                            }.apply {
                                setSlot(
                                    AnvilSlot.INPUT_LEFT,
                                    ItemBuilder(Material.PAPER).setName("编辑第${index + 1}行").build()
                                )
                            }.open()*/
                        })
                )
            }
        }
        refresh()
        gui.line(5)
        gui.guiClick { _, _, _, _, _, _, _, _ ->
            refresh()
            return@guiClick true
        }
        val emptyButton = EmptyButton {
            ItemBuilder(Material.GLASS, 1, 7).setName("").build()
        }
        val strings = Slots.PATTERN_LINE_DEFAULT.patternFun().apply(5)
        strings[strings.size - 1] = "-aaaaaaa-"
        gui.draw()[Slots.pattern(strings, 'a')] = emptyButton
        gui.draw().set(Slot.ofGame(1, 5), Button.of({
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
                gui.title("编辑Lore 第${gui.page()}页")

            }
        }))
        gui.draw().set(Slot.ofGame(9, 5), Button.of({
            refresh()
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
                gui.title("编辑Lore 第${gui.page()}页")
            }
        }))
        gui.draw().set(
            Slot.ofGame(5, 5),
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
                null
            }
        )
            .set(
                Slot.ofGame(6, 5),
                ClickButton(
                    {
                        ItemBuilder(Material.GLOWSTONE_DUST).apply {
                            if (delMode) {
                                setType(Material.REDSTONE)
                                setName("§e当前: §c删除")
                            } else {
                                setName("§e当前: §a编辑")
                            }
                            addLore("")
                            addLore("§e点击切换")
                        }.build()
                    },
                    PlayerSimpleCancelUpdateAllInterface {
                        delMode = !delMode
                        refresh()
                        gui.page(gui.pagination().pages)
                        gui.title("编辑Lore 第${gui.page()}页")
                    })
            )
        return gui
    }
}