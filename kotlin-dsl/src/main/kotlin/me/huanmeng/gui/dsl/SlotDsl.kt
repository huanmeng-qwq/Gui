@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.AbstractGui
import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.button.ClickData
import me.huanmeng.gui.gui.enums.Result
import me.huanmeng.gui.gui.slot.Slot
import me.huanmeng.gui.gui.slot.Slots
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.SlotType

/**
 * Builds a simple [Slot] from a slot index.
 *
 * Creates a basic slot instance representing a position in the inventory.
 * This is useful for simple slot definitions without custom behavior.
 *
 * @param slot The inventory slot index (0-based position)
 * @return A Slot instance representing the specified position
 * @see Slot
 */
fun buildSlot(slot: Int): Slot {
    return Slot.of(slot)
}

/**
 * Builds a [Slot] with custom behavior using a DSL configuration block.
 *
 * This function allows creating slots with custom click handlers and placement rules.
 * The slot can respond to player interactions and control whether items can be placed in it.
 *
 * @param lambda The DSL configuration block for the slot
 * @return A configured Slot instance with custom behavior
 * @throws IllegalStateException if index is not set in the DSL block
 * @see SlotDsl
 */
fun buildSlot(lambda: SlotDsl.() -> Unit): Slot {
    val slotsDsl = SlotDsl().apply(lambda)
    val index = requireNotNull(slotsDsl.index) { "index is null" }
    return SlotWrapper(index, slotsDsl)
}

/**
 * Builds a collection of [Slots] from multiple slot indices.
 *
 * Creates a Slots instance containing multiple slot positions in the inventory.
 * Useful for applying the same button or behavior to multiple slots at once.
 *
 * @param slot Variable number of slot indices to include
 * @return A Slots instance containing all specified slot positions
 * @see Slots
 */
fun buildSlots(vararg slot: Int): Slots {
    return Slots.of(*slot)
}

/**
 * Builds a collection of [Slots] using a DSL configuration block.
 *
 * Provides a flexible way to define multiple slots with custom configurations.
 * Slots can be added individually within the lambda block.
 *
 * @param lambda The DSL configuration block for defining slots
 * @return A Slots instance containing all configured slots
 * @see SlotsDsl
 */
fun buildSlots(lambda: SlotsDsl.() -> Unit): Slots {
    val dsl = SlotsDsl().apply(lambda)
    return Slots.of(*dsl.toTypedArray())
}

/**
 * Builds dynamic [Slots] based on the number of lines in the GUI.
 *
 * This function creates slots that are calculated dynamically based on the GUI's size.
 * The lambda receives the number of inventory lines (rows) and should return a list of slots.
 * This is useful for creating responsive slot layouts that adapt to different inventory sizes.
 *
 * @param lambda Function that receives the line count and returns a list of slots
 * @return A Slots instance that dynamically generates slots based on GUI size
 * @see SlotsDsl
 */
fun buildSlotsByLine(lambda: SlotsDsl.(line: Int) -> List<Slot>): Slots {
    val dsl = SlotsDsl()
    return SlotsWrapper { gui, list ->
        list.addAll(lambda(dsl, gui.size() / 9))
    }
}

/**
 * DSL builder class for configuring custom slot behavior.
 *
 * This class allows defining slots with custom click handlers and placement rules.
 * You can override the default behavior when a slot is clicked or when items are placed in it.
 */
class SlotDsl {
    /**
     * The inventory slot index (0-based position).
     * Must be set when using [buildSlot] with a DSL block.
     */
    var index: Int? = null

    /**
     * Legacy click handler with full event details.
     * Receives all click event parameters individually.
     * Prefer using [onClickData] for a cleaner API.
     */
    var onClick: ((
        button: Button, player: Player, click: ClickType, action: InventoryAction, slotType: SlotType, slot: Int, hotbarKey: Int, e: InventoryClickEvent
    ) -> Result)? = null

    /**
     * Modern click handler using ClickData wrapper.
     * This is the preferred way to handle slot clicks.
     *
     * @see ClickData
     */
    var onClickData: ((
        clickData: ClickData
    ) -> Result)? = null

    /**
     * Handler to control whether items can be placed in this slot.
     * Return true to allow placement, false to deny.
     *
     * This can be used to create read-only slots or implement custom placement logic.
     */
    var tryPlace: ((button: Button, player: Player) -> Boolean)? = null
}

/**
 * Internal wrapper class that implements Slot with custom DSL behavior.
 *
 * This class bridges the DSL configuration ([SlotDsl]) with the actual [Slot] interface.
 */
private class SlotWrapper(private val index: Int, private val slotDsl: SlotDsl) : Slot {
    override fun getIndex(): Int {
        return index
    }

    override fun onClick(clickData: ClickData): Result {
        return if (slotDsl.onClickData != null) {
            slotDsl.onClickData!!(clickData)
        } else super.onClick(clickData)
    }

    override fun tryPlace(button: Button, player: Player): Boolean {
        return if (slotDsl.tryPlace != null) {
            return slotDsl.tryPlace!!(button, player)
        } else super.tryPlace(button, player)
    }
}

/**
 * Internal wrapper class for dynamically calculated slot collections.
 *
 * This wrapper allows slots to be calculated based on GUI properties (like size)
 * rather than being fixed at creation time.
 */
private class SlotsWrapper(
    private val lambda: (gui: AbstractGui<*>, list: MutableList<Slot>) -> Unit
) : Slots {
    override fun <G : AbstractGui<G>> slots(gui: G): Array<Slot> {
        val list: MutableList<Slot> = ArrayList()
        lambda(gui, list)
        return list.toTypedArray()
    }
}

/**
 * A mutable list of [Slot] instances with DSL builder methods.
 *
 * Provides convenient methods for adding slots to a collection using builder patterns.
 *
 * @param list Initial list of slots (defaults to empty list)
 */
class SlotsDsl(list: MutableList<Slot>) : MutableList<Slot> by list {
    constructor() : this(ArrayList())

    /**
     * Adds a slot by evaluating a lambda that returns a Slot.
     *
     * @param lambda Function that creates and returns a Slot
     * @return true if the slot was added successfully
     */
    fun slot(lambda: () -> Slot) = add(lambda())

    /**
     * Adds a slot using a DSL configuration block.
     *
     * @param lambda DSL block for configuring the slot
     * @return true if the slot was added successfully
     * @see buildSlot
     */
    fun slot(lambda: SlotDsl.() -> Unit) = add(buildSlot(lambda))
}

/**
 * Enum defining slot position types for coordinate-based slot creation.
 *
 * This system allows creating slots using intuitive coordinate notation with operator overloading.
 * For example: `1.y + 2.x` creates a slot at row 1, column 2.
 *
 * Coordinate systems:
 * - X/Y: Bukkit-style (row, column) with 0-based indexing
 * - GAME_X/GAME_Y: Game-style (row, column) with 1-based indexing
 *
 * Example:
 * ```kotlin
 * val slot = 0.y + 1.x  // Row 0, Column 1 (Bukkit style)
 * val slot2 = 1.gy + 2.gx  // Row 1, Column 2 (Game style, 1-based)
 * ```
 */
enum class IntSlotType {
    /** X coordinate in Bukkit style (0-based column) */
    X {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    throw IllegalArgumentException("Cannot add X to X")
                }

                Y -> {
                    return Slot.ofBukkit(other.value, this.value)
                }

                GAME_X -> {
                    throw IllegalArgumentException("Cannot add X to GameX")
                }

                GAME_Y -> {
                    return Slot.ofGame(other.value, this.value + 1)
                }
            }
        }
    },
    /** Y coordinate in Bukkit style (0-based row) */
    Y {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    return Slot.ofBukkit(this.value, other.value)
                }

                Y -> {
                    throw IllegalArgumentException("Cannot add Y to X")
                }

                GAME_X -> {
                    return Slot.ofGame(this.value + 1, other.value)
                }

                GAME_Y -> {
                    throw IllegalArgumentException("Cannot add GameY to X")
                }
            }
        }
    },
    /** X coordinate in game style (1-based column) */
    GAME_X {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    throw IllegalArgumentException("Cannot add X to X")
                }

                Y -> {
                    return Slot.ofGame(other.value + 1, this.value)
                }

                GAME_X -> {
                    throw IllegalArgumentException("Cannot add X to GameX")
                }

                GAME_Y -> {
                    return Slot.ofGame(other.value, this.value)
                }
            }
        }
    },
    /** Y coordinate in game style (1-based row) */
    GAME_Y {
        override fun IntSlot.createSlot(other: IntSlot): Slot {
            when (other.type) {
                X -> {
                    return Slot.ofGame(this.value, other.value + 1)
                }

                Y -> {
                    throw IllegalArgumentException("Cannot add GameY to X")
                }

                GAME_X -> {
                    return Slot.ofGame(this.value, other.value)
                }

                GAME_Y -> {
                    throw IllegalArgumentException("Cannot add GameY to GameY")
                }
            }
        }
    },
    ;

    /**
     * Creates a Slot by combining this IntSlot with another.
     * The combination rules depend on the types of both IntSlots.
     */
    abstract fun IntSlot.createSlot(other: IntSlot): Slot
}

/**
 * Represents a single coordinate value with a type (X, Y, GAME_X, or GAME_Y).
 *
 * This class enables coordinate-based slot creation using operator overloading.
 * Supports arithmetic operations and combination with other IntSlots to create Slots.
 *
 * @property value The coordinate value
 * @property type The coordinate type (X, Y, GAME_X, or GAME_Y)
 */
class IntSlot(val value: Int, val type: IntSlotType) {
    /**
     * Combines this IntSlot with another to create a Slot.
     * Example: `1.y + 2.x` creates a slot at row 1, column 2.
     */
    operator fun plus(other: IntSlot): Slot {
        return type.run { createSlot(other) }
    }

    /** Adds a value to this coordinate */
    operator fun plus(other: Int): IntSlot {
        return IntSlot(value + other, type)
    }

    /** Subtracts a value from this coordinate */
    operator fun minus(other: Int): IntSlot {
        return IntSlot(value - other, type)
    }

    /** Multiplies this coordinate by a value */
    operator fun times(other: Int): IntSlot {
        return IntSlot(value * other, type)
    }

    /** Divides this coordinate by a value */
    operator fun div(other: Int): IntSlot {
        return IntSlot(value / other, type)
    }

    /** Calculates modulo of this coordinate */
    operator fun rem(other: Int): IntSlot {
        return IntSlot(value % other, type)
    }

    override fun toString(): String {
        return "IntSlot(value=$value, type=$type)"
    }
}

/**
 * Extension property to create an X coordinate (Bukkit style, 0-based column).
 * Example: `5.x` represents column 5.
 */
val Int.x: IntSlot get() = IntSlot(this, IntSlotType.X)

/**
 * Extension property to create a Y coordinate (Bukkit style, 0-based row).
 * Example: `2.y` represents row 2.
 */
val Int.y: IntSlot get() = IntSlot(this, IntSlotType.Y)

/**
 * Extension property to create a GAME_X coordinate (1-based column).
 * Example: `5.gx` represents column 5 in game coordinates.
 */
val Int.gx: IntSlot get() = IntSlot(this, IntSlotType.GAME_X)

/**
 * Extension property to create a GAME_Y coordinate (1-based row).
 * Example: `2.gy` represents row 2 in game coordinates.
 */
val Int.gy: IntSlot get() = IntSlot(this, IntSlotType.GAME_Y)