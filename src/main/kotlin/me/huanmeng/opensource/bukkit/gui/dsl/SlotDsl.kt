@file:Suppress("unused")

package me.huanmeng.opensource.bukkit.gui.dsl

import me.huanmeng.opensource.bukkit.gui.AbstractGui
import me.huanmeng.opensource.bukkit.gui.button.Button
import me.huanmeng.opensource.bukkit.gui.enums.Result
import me.huanmeng.opensource.bukkit.gui.slot.Slot
import me.huanmeng.opensource.bukkit.gui.slot.Slots
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.SlotType

/**
 * 构建[Slot]
 * @param slot slot index
 */
fun buildSlot(slot: Int): Slot {
    return Slot.of(slot)
}

/**
 * 构建[Slot]
 */
fun buildSlot(lambda: SlotDsl.() -> Unit): Slot {
    val slotsDsl = SlotDsl().apply(lambda)
    val index = requireNotNull(slotsDsl.index) { "index is null" }
    return SlotWrapper(index, slotsDsl)
}

/**
 * 构建[Slots]
 */
fun buildSlots(vararg slot: Int): Slots {
    return Slots.of(*slot)
}

/**
 * 构建 [Slots]
 */
fun buildSlots(lambda: SlotsDsl.() -> Unit): Slots {
    val dsl = SlotsDsl().apply(lambda)
    return Slots.of(*dsl.toTypedArray())
}

/**
 * 通过行数来决定构建[Slots]
 */
fun buildSlotsByLine(lambda: SlotsDsl.(line: Int) -> List<Slot>): Slots {
    val dsl = SlotsDsl()
    return SlotsWrapper { gui, list ->
        list.addAll(lambda(dsl, gui.size() / 9))
    }
}

class SlotDsl {
    var index: Int? = null
    var onClick: ((
        button: Button, player: Player, click: ClickType, action: InventoryAction, slotType: SlotType,
        slot: Int, hotbarKey: Int, e: InventoryClickEvent
    ) -> Result)? = null
    var tryPlace: ((button: Button, player: Player) -> Boolean)? = null
}

private class SlotWrapper(private val index: Int, private val slotDsl: SlotDsl) : Slot {
    override fun getIndex(): Int {
        return index
    }

    override fun onClick(
        button: Button,
        player: Player,
        click: ClickType,
        action: InventoryAction,
        slotType: SlotType,
        slot: Int,
        hotBarKey: Int,
        e: InventoryClickEvent
    ): Result {
        return if (slotDsl.onClick != null) {
            return slotDsl.onClick!!(button, player, click, action, slotType, slot, hotBarKey, e)
        } else super.onClick(button, player, click, action, slotType, slot, hotBarKey, e)
    }

    override fun tryPlace(button: Button, player: Player): Boolean {
        return if (slotDsl.tryPlace != null) {
            return slotDsl.tryPlace!!(button, player)
        } else super.tryPlace(button, player)
    }
}

private class SlotsWrapper(
    private val lambda: (gui: AbstractGui<*>, list: MutableList<Slot>) -> Unit
) : Slots {
    override fun <G : AbstractGui<G>> slots(gui: G): Array<Slot> {
        val list: MutableList<Slot> = ArrayList()
        lambda(gui, list)
        return list.toTypedArray()
    }
}

class SlotsDsl(list: MutableList<Slot>) : MutableList<Slot> by list {
    constructor() : this(ArrayList())

    fun slot(lambda: () -> Slot) = add(lambda())

    fun slot(lambda: SlotDsl.() -> Unit) = add(buildSlot(lambda))
}