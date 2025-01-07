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
        button: Button, player: Player, click: ClickType, action: InventoryAction, slotType: SlotType, slot: Int, hotbarKey: Int, e: InventoryClickEvent
    ) -> Result)? = null
    var tryPlace: ((button: Button, player: Player) -> Boolean)? = null
}

private class SlotWrapper(private val index: Int, private val slotDsl: SlotDsl) : Slot {
    override fun getIndex(): Int {
        return index
    }

    override fun onClick(
        gui: AbstractGui<*>,
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
        } else super.onClick(gui, button, player, click, action, slotType, slot, hotBarKey, e)
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

enum class IntSlotType {
    X {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    throw IllegalArgumentException("Cannot add X to X")
                }

                Y -> {
                    return Slot.ofBukkit(this.value, other.value)
                }

                GAME_X -> {
                    throw IllegalArgumentException("Cannot add X to GameX")
                }

                GAME_Y -> {
                    return Slot.ofGame(this.value + 1, other.value)
                }
            }
        }
    },
    Y {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    return Slot.ofBukkit(other.value, this.value)
                }

                Y -> {
                    throw IllegalArgumentException("Cannot add Y to X")
                }

                GAME_X -> {
                    return Slot.ofGame(other.value, this.value + 1)
                }

                GAME_Y -> {
                    throw IllegalArgumentException("Cannot add GameY to X")
                }
            }
        }
    },
    GAME_X {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    throw IllegalArgumentException("Cannot add X to X")
                }

                Y -> {
                    return Slot.ofGame(this.value, other.value + 1)
                }

                GAME_X -> {
                    throw IllegalArgumentException("Cannot add X to GameX")
                }

                GAME_Y -> {
                    return Slot.ofGame(this.value, other.value)
                }
            }
        }
    },
    GAME_Y {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    return Slot.ofGame(other.value + 1, this.value)
                }

                Y -> {
                    throw IllegalArgumentException("Cannot add GameY to X")
                }

                GAME_X -> {
                    return Slot.ofGame(other.value, this.value)
                }

                GAME_Y -> {
                    throw IllegalArgumentException("Cannot add GameY to GameY")
                }
            }
        }
    },
    ;

    abstract fun IntSlot.createSlot(other: IntSlot): Slot
}

class IntSlot(val value: Int, val type: IntSlotType) {
    operator fun plus(other: IntSlot): Slot {
        return type.run { createSlot(other) }
    }
    operator fun plus(other: Int): IntSlot {
        return IntSlot(value+ other, type)
    }
    operator fun minus(other: Int): IntSlot {
        return IntSlot(value - other, type)
    }
    operator fun times(other: Int): IntSlot {
        return IntSlot(value * other, type)
    }
    operator fun div(other: Int): IntSlot {
        return IntSlot(value / other, type)
    }
    operator fun rem(other: Int): IntSlot {
        return IntSlot(value % other, type)
    }

    override fun toString(): String {
        return "IntSlot(value=$value, type=$type)"
    }
}

val Int.x: IntSlot get() = IntSlot(this, IntSlotType.X)
val Int.y: IntSlot get() = IntSlot(this, IntSlotType.Y)
val Int.gx: IntSlot get() = IntSlot(this, IntSlotType.GAME_X)
val Int.gy: IntSlot get() = IntSlot(this, IntSlotType.GAME_Y)