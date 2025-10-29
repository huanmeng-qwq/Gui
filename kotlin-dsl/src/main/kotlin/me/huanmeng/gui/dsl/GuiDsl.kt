@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.AbstractGui
import me.huanmeng.gui.gui.GuiButton
import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.draw.GuiDraw
import me.huanmeng.gui.gui.impl.AbstractGuiCustom
import me.huanmeng.gui.gui.impl.CustomGuiPage
import me.huanmeng.gui.gui.impl.GuiCustom
import me.huanmeng.gui.gui.impl.GuiPage
import me.huanmeng.gui.gui.impl.page.PageButton
import me.huanmeng.gui.gui.impl.page.PageButtonTypes
import me.huanmeng.gui.gui.impl.page.PageSetting
import me.huanmeng.gui.gui.impl.page.PageSettings
import me.huanmeng.gui.gui.slot.Slot
import me.huanmeng.gui.gui.slot.Slots
import org.bukkit.entity.Player
import kotlin.math.min

/**
 * Builds a [GuiCustom] using a DSL configuration block.
 *
 * GuiCustom is a basic GUI type for displaying custom inventory layouts with buttons.
 * This function creates an empty GUI that can be configured with slots, buttons, and behaviors.
 *
 * Example:
 * ```kotlin
 * val gui = buildGui {
 *     line(3)  // 3 rows
 *     title("My GUI")
 *     draw {
 *         slot(0) button ItemStack(Material.DIAMOND).asButton
 *     }
 * }
 * ```
 *
 * @param lambda The DSL configuration block for the GUI
 * @return A configured GuiCustom instance
 * @see GuiCustom
 */
fun buildGui(lambda: GuiCustom.() -> Unit): GuiCustom {
    return GuiCustom().apply(lambda)
}

/**
 * Builds a [GuiPage] using a DSL configuration block.
 *
 * GuiPage is a paginated GUI type for displaying large collections of items across multiple pages.
 * The DSL must set [elementSlots] and [allItems] properties or this function will throw an exception.
 *
 * Example:
 * ```kotlin
 * val gui = buildPagedGui {
 *     line(6)
 *     title("Items")
 *     allItems = myButtonList
 *     elementSlots = buildSlots(0, 1, 2, 3, 4, 5, 6, 7, 8)
 *     simplePageSetting()
 * }
 * ```
 *
 * @param lambda The DSL configuration block for the paginated GUI
 * @return A configured GuiPage instance
 * @throws IllegalStateException if elementSlots or allItems are not set
 * @see GuiPage
 */
fun buildPagedGui(lambda: GuiPage.() -> Unit): GuiPage {
    return GuiPage().apply(lambda).apply {
        // check is set?
        requireNotNull(elementSlots) {
            "elementSlots is null"
        }
        requireNotNull(allItems) {
            "allItems is null"
        }
    }
}

/**
 * Builds a [CustomGuiPage] using a DSL configuration block.
 *
 * CustomGuiPage is an advanced paginated GUI with support for multiple page areas.
 * This allows different sections of the inventory to display different paginated content.
 *
 * @param lambda The DSL configuration block for the custom paginated GUI
 * @return A configured CustomGuiPage instance
 * @see CustomGuiPage
 */
fun buildCustomPage(lambda: CustomGuiPage.() -> Unit): CustomGuiPage {
    return CustomGuiPage().apply(lambda)
}

/* Player Extension Functions */

/**
 * Opens a GUI for this player.
 *
 * This extension function sets the player on the GUI and opens it.
 * The GUI must already be configured before calling this function.
 *
 * @param gui The GUI to open for this player
 */
fun Player.openGui(gui: AbstractGui<*>) {
    gui.apply { player = this@openGui }.openGui()
}

/**
 * Creates and opens a [GuiCustom] for this player using a DSL block.
 *
 * This is a convenient way to create and immediately open a GUI in one step.
 *
 * Example:
 * ```kotlin
 * player.openGui {
 *     line(3)
 *     title("My GUI")
 *     draw {
 *         slot(0) button ItemStack(Material.DIAMOND).asButton
 *     }
 * }
 * ```
 *
 * @param lambda The DSL configuration block for the GUI
 */
fun Player.openGui(lambda: AbstractGuiCustom<*>.() -> Unit) = openGui(buildGui(lambda))

/**
 * Creates and opens a [GuiPage] for this player using a DSL block.
 *
 * This is a convenient way to create and immediately open a paginated GUI in one step.
 *
 * @param lambda The DSL configuration block for the paginated GUI
 */
fun Player.openPagedGui(lambda: GuiPage.() -> Unit) = openGui(buildPagedGui(lambda))

/* GuiCustom Extension Functions */

/**
 * Adds multiple buttons to the GUI with automatic slot assignment.
 *
 * Buttons are added sequentially starting from slot 0.
 * The index of each button in the list determines its slot position.
 *
 * Example:
 * ```kotlin
 * gui.buttons {
 *     button { item = ItemStack(Material.DIAMOND) }  // Slot 0
 *     button { item = ItemStack(Material.EMERALD) }  // Slot 1
 *     button { item = ItemStack(Material.GOLD_INGOT) }  // Slot 2
 * }
 * ```
 *
 * @param lambda The DSL block for building the button list
 */
fun AbstractGuiCustom<*>.buttons(lambda: ButtonList.() -> Unit) {
    val arrayList = ButtonList()
    lambda(arrayList)
    arrayList.forEachIndexed { index, btn ->
        addAttachedButton(GuiButton(buildSlot(index), btn))
    }
}

/**
 * Provides access to the GUI's drawing API for placing buttons.
 *
 * The drawing API allows placing buttons at specific slots or ranges of slots.
 *
 * @param lambda The DSL block for drawing operations
 */
fun <G : AbstractGuiCustom<G>> G.draw(lambda: GuiDraw<G>.() -> Unit) = lambda(draw())

/**
 * Provides access to the GUI's drawing API for placing buttons.
 *
 * This overload works with any AbstractGui type.
 *
 * @param lambda The DSL block for drawing operations
 */
fun <G : AbstractGui<G>> G.draw(lambda: GuiDraw<G>.() -> Unit) = lambda(draw())

/**
 * Opens this GUI for a specific player.
 *
 * Sets the player on the GUI and opens it immediately.
 *
 * @param player The player to open the GUI for
 */
fun AbstractGuiCustom<*>.openGui(player: Player) {
    this.player = player
    openGui()
}

/* GuiPage Extension Functions */

/**
 * Sets the [PageSetting] for a [GuiPage] using a lambda that returns the setting.
 *
 * This is a convenient way to set page settings inline.
 *
 * Example:
 * ```kotlin
 * gui.pageSetting { PageSettings.normal(this) }
 * ```
 *
 * @param lambda Function that returns the PageSetting to use
 */
fun GuiPage.pageSetting(lambda: () -> PageSetting) {
    pageSetting(lambda())
}

/**
 * Applies the default page setting to a [GuiPage].
 *
 * This uses [PageSettings.normal] to configure standard navigation buttons
 * (previous, next, first, last) at common positions.
 *
 * Example:
 * ```kotlin
 * gui.simplePageSetting()
 * ```
 *
 * @see PageSettings.normal
 */
fun GuiPage.simplePageSetting() {
    pageSetting(PageSettings.normal(this))
}
/* end */

/* GuiDraw Extension Functions */

/**
 * Sets a button at a specific slot using a DSL configuration block.
 *
 * This is a shorthand for creating and placing a button in one step.
 *
 * @param slot The slot position where the button should be placed
 * @param lambda The DSL configuration block for the button
 */
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slot: Slot, lambda: ButtonDsl.() -> Unit) {
    set(slot, buildButton(lambda))
}

/**
 * Sets a button across multiple slots using a DSL configuration block.
 *
 * The same button will be placed in all specified slots.
 *
 * @param slots The collection of slots where the button should be placed
 * @param lambda The DSL configuration block for the button
 */
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slots: Slots, lambda: ButtonDsl.() -> Unit) {
    set(slots, buildButton(lambda))
}

/**
 * Sets multiple buttons across a collection of slots.
 *
 * Each button in the list will be placed at the corresponding slot.
 *
 * @param slots The collection of slots to fill
 * @param lambda DSL block for building the button list
 */
fun GuiDraw<out AbstractGuiCustom<*>>.setButton(slots: Slots, lambda: ButtonList.() -> Unit) =
    set(slots, ButtonList().apply(lambda))

/**
 * Helper class for the infix DSL syntax: `slot(0) button myButton`.
 *
 * This class enables a more readable syntax for placing buttons at specific slots.
 *
 * @param G The GUI type
 * @property draw The GuiDraw instance to operate on
 * @property slot The target slot for button placement
 */
class SetButton<G : AbstractGui<G>>(private val draw: GuiDraw<G>, private val slot: Slot) {
    /**
     * Places a button at the slot using infix notation.
     *
     * Example: `slot(0) button myButton`
     */
    infix fun button(button: Button) {
        draw.set(slot, button)
    }
}

/**
 * Helper class for the infix DSL syntax: `slots(...) button myButton`.
 *
 * This class enables a more readable syntax for placing buttons across multiple slots.
 *
 * @param G The GUI type
 * @property draw The GuiDraw instance to operate on
 * @property slots The target slots for button placement
 */
class SetButtons<G : AbstractGui<G>>(private val draw: GuiDraw<G>, private val slots: Slots) {
    /**
     * Places a single button across all slots using infix notation.
     *
     * Example: `slots(mySlots) button myButton`
     */
    infix fun button(button: Button) {
        draw.set(slots, button)
    }

    /**
     * Places multiple buttons across the slots using infix notation.
     *
     * Example: `slots(mySlots) buttons { button { ... } }`
     */
    infix fun buttons(lambda: ButtonList.() -> Unit) {
        draw.set(slots, ButtonList().apply(lambda))
    }
}

/**
 * Creates a SetButton instance for a slot index.
 *
 * Enables infix syntax: `slot(0) button myButton`
 */
infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Int): SetButton<G> {
    return SetButton(this, Slot.of(slot))
}

/**
 * Creates a SetButton instance for a Slot object.
 *
 * Enables infix syntax: `slot(mySlot) button myButton`
 */
infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Slot): SetButton<G> {
    return SetButton(this, slot)
}

/**
 * Creates a SetButton instance for a Bukkit-style coordinate pair (row, column).
 *
 * Enables infix syntax: `slot(Pair(1, 2)) button myButton`
 * The pair represents (row, column) in 0-based Bukkit coordinates.
 */
infix fun <G : AbstractGui<G>> GuiDraw<G>.slot(slot: Pair<Int, Int>): SetButton<G> {
    return SetButton(this, Slot.ofBukkit(slot.first, slot.second))
}

/**
 * Creates a SetButton instance for a game-style coordinate pair (row, column).
 *
 * Enables infix syntax: `gameSlot(Pair(1, 2)) button myButton`
 * The pair represents (row, column) in 1-based game coordinates.
 */
infix fun <G : AbstractGui<G>> GuiDraw<G>.gameSlot(slot: Pair<Int, Int>): SetButton<G> {
    return SetButton(this, Slot.ofGame(slot.first, slot.second))
}

/**
 * Creates a SetButtons instance for a Slots collection.
 *
 * Enables infix syntax: `slots(mySlots) button myButton` or `slots(mySlots) buttons { ... }`
 */
infix fun <G : AbstractGui<G>> GuiDraw<G>.slots(slots: Slots): SetButtons<G> {
    return SetButtons(this, slots)
}
/* end */

/* Pattern-based GUI Construction */

/**
 * Entry point object for pattern-based GUI construction.
 *
 * Provides functions to create GUIs using ASCII art patterns where each character
 * represents a different button type or slot configuration.
 *
 * Example:
 * ```kotlin
 * Guis.of(
 *     "XXXXXXXXX",
 *     "X       X",
 *     "XXXXXXXXX"
 * ) {
 *     'X' map borderButton
 *     ' ' map emptyButton
 * }
 * ```
 */
object Guis

/**
 * DSL class for pattern-based GUI configuration.
 *
 * This class allows mapping characters in ASCII art patterns to buttons,
 * creating visually intuitive GUI layouts.
 *
 * @param G The GUI type
 * @property gui The GUI instance being configured
 * @property pattern The ASCII art pattern defining the layout
 */
open class PatternCustomGuiDsl<G : AbstractGuiCustom<G>>(open val gui: G, open val pattern: Array<out String>) {
    /**
     * Maps a single character to a button.
     *
     * All occurrences of this character in the pattern will display the button.
     *
     * Example: `'X' map borderButton`
     */
    infix fun Char.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, this@map), button)
        }
    }

    /**
     * Maps a string of characters to a button.
     *
     * All occurrences of any character in the string will display the button.
     *
     * Example: `"XY" map borderButton`
     */
    infix fun String.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, *toCharArray()), button)
        }
    }

    /**
     * Maps multiple strings to a button.
     *
     * All characters from all strings will be mapped to the button.
     */
    infix fun Array<out String>.map(button: Button) {
        val chatArray = this@map.flatMap { it.toCharArray().toList() }.toCharArray()
        chatArray.map(button)
    }

    /**
     * Maps an array of characters to a button.
     *
     * All specified characters will display the button.
     */
    infix fun Array<Char>.map(button: Button) {
        this@map.toCharArray().map(button)
    }

    /**
     * Maps a CharArray to a button.
     *
     * All specified characters will display the button.
     */
    infix fun CharArray.map(button: Button) {
        gui.draw {
            set(Slots.pattern(pattern, *this@map), button)
        }
    }

    /**
     * Maps each string in a list to a button.
     *
     * Each string is processed independently, mapping its characters to the button.
     */
    infix fun List<String>.mapString(button: Button) {
        forEach {
            it.map(button)
        }
    }

    /**
     * Maps each character in a list to a button.
     *
     * Each character is processed independently.
     */
    infix fun List<Char>.mapChar(button: Button) {
        forEach {
            it.map(button)
        }
    }

    /**
     * Provides access to the underlying GUI for additional configuration.
     *
     * @param lambda Configuration block for the GUI
     */
    fun gui(lambda: AbstractGuiCustom<*>.() -> Unit) {
        gui.lambda()
    }
}

/**
 * DSL class for pattern-based paginated GUI configuration.
 *
 * Extends [PatternCustomGuiDsl] with page-specific functionality for configuring
 * navigation buttons (next, previous, first, last).
 *
 * @property gui The GuiPage instance being configured
 * @property pattern The ASCII art pattern defining the layout
 */
class PatternPageGuiDsl(override val gui: GuiPage, override val pattern: Array<out String>) :
    PatternCustomGuiDsl<GuiPage>(gui, pattern) {

    /**
     * Provides access to the underlying GuiPage for additional configuration.
     *
     * @param lambda Configuration block for the GuiPage
     */
    fun page(lambda: GuiPage.() -> Unit) {
        gui.lambda()
    }

    /**
     * Configures a "next page" button for the PageSetting.
     *
     * Example: `pageSetting next myButton`
     */
    infix fun PageSetting.next(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.NEXT).build() })
    }

    /**
     * Configures a "previous page" button for the PageSetting.
     *
     * Example: `pageSetting prev myButton`
     */
    infix fun PageSetting.prev(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.PREVIOUS).build() })
    }

    /**
     * Configures a "last page" button for the PageSetting.
     *
     * Example: `pageSetting last myButton`
     */
    infix fun PageSetting.last(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.LAST).build() })
    }

    /**
     * Configures a "first page" button for the PageSetting.
     *
     * Example: `pageSetting first myButton`
     */
    infix fun PageSetting.first(button: Button) {
        pageButtons().add({ PageButton.builder(gui).button(button).types(PageButtonTypes.FIRST).build() })
    }
}

/**
 * Creates a [GuiCustom] using ASCII art patterns.
 *
 * This function allows defining GUI layouts visually using multi-line strings where
 * each character represents a different button or slot type.
 *
 * Example:
 * ```kotlin
 * val gui = Guis.of(
 *     "XXXXXXXXX",
 *     "X       X",
 *     "XXXXXXXXX"
 * ) {
 *     'X' map borderButton
 *     ' ' map Button.EMPTY
 * }
 * ```
 *
 * @param patterns Variable number of pattern strings (one per inventory row)
 * @param lambda DSL configuration block for mapping characters to buttons
 * @return A configured GuiCustom instance
 */
fun Guis.of(vararg patterns: String, lambda: PatternCustomGuiDsl<GuiCustom>.() -> Unit): GuiCustom {
    return GuiCustom().apply {
        line(min(9, patterns.size))

    }.also {
        PatternCustomGuiDsl(it, patterns).lambda()
    }
}

/**
 * Creates a [GuiPage] using ASCII art patterns.
 *
 * This function allows defining paginated GUI layouts visually. The patterns define
 * the overall layout, and you must still configure [elementSlots] and [allItems].
 *
 * Example:
 * ```kotlin
 * val gui = Guis.ofPage(
 *     "XXXXXXXXX",
 *     "OOOOOOOOO",  // O = paginated items
 *     "XP     NX"   // P = previous, N = next
 * ) {
 *     'X' map borderButton
 *     page {
 *         elementSlots = Slots.pattern(pattern, 'O')
 *         allItems = myItems
 *     }
 * }
 * ```
 *
 * @param patterns Variable number of pattern strings (one per inventory row)
 * @param lambda DSL configuration block for mapping characters and configuring pagination
 * @return A configured GuiPage instance
 * @throws IllegalStateException if elementSlots or allItems are not set
 */
fun Guis.ofPage(vararg patterns: String, lambda: PatternPageGuiDsl.() -> Unit): GuiPage {
    return GuiPage().apply {
        line(min(9, patterns.size))
    }.also {
        PatternPageGuiDsl(it, patterns).lambda()
    }.apply {
        // check is set?
        requireNotNull(elementSlots) {
            "elementSlots is null"
        }
        requireNotNull(allItems) {
            "allItems is null"
        }
    }
}