package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.HGui
import me.huanmeng.opensource.bukkit.gui.button.ClickButton
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.enchant.EnchantEditor
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.flag.FlagEditor
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.lore.LoreEditor
import me.huanmeng.opensource.bukkit.gui.button.editor.itemstack.material.MaterialEditor
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickCancelUpdateAllInterface
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerSimpleCancelInterface
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerSimpleCancelUpdateAllInterface
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder
import me.huanmeng.opensource.bukkit.util.item.builder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
class ItemStackEditor(
    player: Player, private var itemBuilder: ItemBuilder,
    val editName: ((itemStackEditor: ItemStackEditor, String?) -> Unit)?,
) : HGui(player) {
    var pattern = arrayOf(
        "aaaacaaaa",  // name lore amount dur
        "acacacaca",
        // ench flag
        "aeaeaaaaa",
        "aaaaaaaaa",
        "aaaaaaaaa",
        // back
        "aaaabaaaa"
    )
    var callback: ItemStackCallback? = null

    constructor(player: Player, base: ItemStack) : this(player, base.builder(), null)

    override fun gui(): AbstractGui<in GuiCustom> {
        val editor = this
        val gui = GuiCustom(context.player)
        gui.line(6).title("编辑物品")
        // line 1
        run {
            gui.draw()[Slots.pattern(pattern, 'c')] = listOf(
                ClickButton(
                    { itemBuilder.build() },
                    PlayerSimpleCancelInterface {
                        MaterialEditor(
                            it,
                            itemBuilder.copy(),
                            Material.values().toList()
                        ).apply {
                            callback =
                                ItemStackCallback(
                                    "§e返回至编辑物品: " + itemBuilder.name
                                ) { item ->
                                    itemBuilder = ItemBuilder(item)
                                    editor.open()
                                }
                        }.open()
                    }),
                ClickButton({
                    ItemBuilder(Material.NAME_TAG).setName(
                        "§a编辑名字"
                    ).build()
                },
                    PlayerSimpleCancelUpdateAllInterface {
                        gui.onClose()
                        editName?.invoke(this, itemBuilder.name)
                        /*NmsHelper.get().createAnvil(it) { e ->
                            if (e.slot != AnvilSlot.OUTPUT) {
                                return@createAnvil
                            }
                            e.willDestroy = true
                            itemBuilder.setName(e.name.color())
                            editor.open()
                        }.apply {
                            setSlot(AnvilSlot.INPUT_LEFT, ItemBuilder(itemBuilder.build()).apply {
                                val unColor = name.unColor()
                                setName(unColor.ifEmpty { null })
                            }.build().apply {
                                val itemMeta = itemMeta
                                itemMeta.displayName = itemBuilder.name.unColor()
                                this.itemMeta = itemMeta
                            })
                        }.open()*/
                    }
                ),
                ClickButton({
                    ItemBuilder(Material.PAPER).setName(
                        "§b编辑Lore"
                    ).build()
                },
                    PlayerSimpleCancelInterface {
                        LoreEditor(it, itemBuilder.copy(), null, null).apply {
                            callback =
                                ItemStackCallback(
                                    "§e返回至编辑物品: " + itemBuilder.name
                                ) { item ->
                                    itemBuilder = ItemBuilder(item)
                                    editor.open()
                                }
                        }.open()
                    }),
                ClickButton({
                    ItemBuilder(Material.ARROW).setName("§a修改数量").setAmount(itemBuilder.build().amount).apply {
                        addLore("§7左键+1")
                        addLore("§7右键-1")
                    }.build()
                },
                    PlayerClickCancelUpdateAllInterface { _, click, _, _, _, _ ->
                        val amount = itemBuilder.build().amount
                        if (click.isLeftClick) {
                            itemBuilder.setAmount(min(64, amount + 1))
                        } else if (click.isRightClick) {
                            itemBuilder.setAmount(max(1, amount - 1))
                        }
                    }
                ),
                ClickButton(
                    {
                        ItemBuilder(itemBuilder.build()).apply {
                            setName("§e修改耐久度")
                            clearLores()
                            addLore("§7左键+1")
                            addLore("§7右键-1")
                        }.build()
                    },
                    PlayerClickCancelUpdateAllInterface { user, click, action, slotType, slot, hotBarKey ->
                        val durability = itemBuilder.build().durability
                        if (click.isLeftClick) {
                            itemBuilder.setDurability(durability + 1)
                        } else if (click.isRightClick) {
                            itemBuilder.setDurability(max(0, durability - 1))
                        }
                    })
            )
        }
        // line 2
        run {
            gui.draw()[Slots.pattern(pattern, 'e')] = listOf(
                ClickButton({
                    ItemBuilder(Material.LEGACY_ENCHANTMENT_TABLE).setName(
                        "§b编辑附魔"
                    ).build()
                },
                    PlayerSimpleCancelInterface {
                        EnchantEditor(it, itemBuilder.copy(), Enchantment.values().toList()).apply {
                            callback =
                                ItemStackCallback(
                                    "§e返回至编辑物品: " + itemBuilder.name
                                ) { item ->
                                    itemBuilder = ItemBuilder(item)
                                    editor.open()
                                }
                        }.open()
                    }),
                ClickButton({
                    ItemBuilder(Material.PAPER).setName(
                        "§b编辑Flag"
                    ).build()
                },
                    PlayerSimpleCancelInterface {
                        FlagEditor(it, itemBuilder.copy(), ItemFlag.values().toList()).apply {
                            callback =
                                ItemStackCallback(
                                    "§e返回至编辑物品: " + itemBuilder.name
                                ) { item ->
                                    itemBuilder = ItemBuilder(item)
                                    editor.open()
                                }
                        }.open()
                    }),
            )
        }
        gui.draw()[Slots.pattern(pattern, 'b')] = listOf(
            if (callback != null) {
                ClickButton({
                    return@ClickButton ItemBuilder(Material.HOPPER).setName(callback!!.display())
                        .addLore("§7点击应用并返回")
                        .build()
                },
                    PlayerSimpleCancelInterface {
                        gui.close()
                        callback?.call(itemBuilder.build())
                    })
            } else {
                null
            }
        )
        return gui
    }
}