@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.gui.button.Button
import me.huanmeng.gui.gui.impl.AbstractGuiCustom
import me.huanmeng.gui.gui.impl.GuiCustom
import me.huanmeng.gui.gui.impl.GuiPage
import me.huanmeng.gui.gui.slot.Slot
import me.huanmeng.gui.gui.slot.Slots
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.sqrt

/**
 * Creates a checkerboard pattern with alternating buttons.
 *
 * Example:
 * ```kotlin
 * gui.checkerboard(
 *     button1 = ItemStack(Material.WHITE_STAINED_GLASS_PANE).asButton,
 *     button2 = ItemStack(Material.BLACK_STAINED_GLASS_PANE).asButton
 * )
 * ```
 *
 * @param button1 First button in the checkerboard pattern
 * @param button2 Second button in the checkerboard pattern
 */
fun <G : AbstractGuiCustom<G>> G.checkerboard(button1: Button, button2: Button) {
    val totalSlots = size()
    val rows = totalSlots / 9

    for (row in 0 until rows) {
        for (col in 0..8) {
            val button = if ((row + col) % 2 == 0) button1 else button2
            draw().set(Slot.ofBukkit(row, col), button)
        }
    }
}

/**
 * Creates a diagonal line from top-left to bottom-right.
 *
 * Example:
 * ```kotlin
 * gui.diagonalLine(ItemStack(Material.GOLD_INGOT).asButton)
 * ```
 *
 * @param button The button to use for the diagonal line
 */
fun <G : AbstractGuiCustom<G>> G.diagonalLine(button: Button) {
    val rows = size() / 9
    val maxIndex = minOf(rows, 9)

    for (i in 0 until maxIndex) {
        draw().set(Slot.ofBukkit(i, i), button)
    }
}

/**
 * Creates a frame (border with corners) using different buttons for sides and corners.
 *
 * Example:
 * ```kotlin
 * gui.frame(
 *     sideButton = ItemStack(Material.IRON_BARS).asButton,
 *     cornerButton = ItemStack(Material.GOLD_BLOCK).asButton
 * )
 * ```
 *
 * @param sideButton Button for the sides
 * @param cornerButton Button for the corners
 */
fun <G : AbstractGuiCustom<G>> G.frame(sideButton: Button, cornerButton: Button) {
    val lines = size() / 9
    if (lines < 2) return

    draw {
        // Corners
        set(Slot.ofBukkit(0, 0), cornerButton)
        set(Slot.ofBukkit(0, 8), cornerButton)
        set(Slot.ofBukkit(lines - 1, 0), cornerButton)
        set(Slot.ofBukkit(lines - 1, 8), cornerButton)

        // Top and bottom sides (excluding corners)
        for (col in 1..7) {
            set(Slot.ofBukkit(0, col), sideButton)
            set(Slot.ofBukkit(lines - 1, col), sideButton)
        }

        // Left and right sides (excluding corners)
        if (lines > 2) {
            for (row in 1 until lines - 1) {
                set(Slot.ofBukkit(row, 0), sideButton)
                set(Slot.ofBukkit(row, 8), sideButton)
            }
        }
    }
}

/**
 * Creates a grid layout with specific spacing between buttons.
 *
 * Example:
 * ```kotlin
 * gui.grid(
 *     startRow = 1,
 *     startCol = 1,
 *     rows = 3,
 *     cols = 3,
 *     spacing = 1
 * ) { row, col ->
 *     ItemStack(Material.DIAMOND).asButton
 * }
 * ```
 *
 * @param startRow Starting row (0-based)
 * @param startCol Starting column (0-based)
 * @param rows Number of rows in the grid
 * @param cols Number of columns in the grid
 * @param spacing Space between grid items (in slots)
 * @param buttonProvider Lambda that provides the button for each grid position
 */
fun <G : AbstractGuiCustom<G>> G.grid(
    startRow: Int,
    startCol: Int,
    rows: Int,
    cols: Int,
    spacing: Int = 1,
    buttonProvider: (row: Int, col: Int) -> Button
) {
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val actualRow = startRow + (r * (spacing + 1))
            val actualCol = startCol + (c * (spacing + 1))

            if (actualRow >= size() / 9 || actualCol > 8) continue

            draw().set(Slot.ofBukkit(actualRow, actualCol), buttonProvider(r, c))
        }
    }
}

/**
 * Fills a circular area (approximated) with a button.
 *
 * Example:
 * ```kotlin
 * gui.circle(
 *     centerRow = 2,
 *     centerCol = 4,
 *     radius = 2,
 *     button = ItemStack(Material.GLOWSTONE).asButton
 * )
 * ```
 *
 * @param centerRow Center row of the circle
 * @param centerCol Center column of the circle
 * @param radius Radius of the circle in slots
 * @param button Button to fill the circle with
 */
fun <G : AbstractGuiCustom<G>> G.circle(
    centerRow: Int,
    centerCol: Int,
    radius: Int,
    button: Button
) {
    val rows = size() / 9

    for (row in 0 until rows) {
        for (col in 0..8) {
            val dx = col - centerCol
            val dy = row - centerRow
            val distance = sqrt((dx * dx + dy * dy).toDouble())

            if (distance <= radius) {
                draw().set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Pagination helper that automatically creates a paginated GUI from a list of items.
 *
 * Example:
 * ```kotlin
 * val items = listOf<ItemStack>(...)
 * val gui = items.toPagedGui(player, "Items") {
 *     // Optional customization
 *     border(ItemStack(Material.BLACK_STAINED_GLASS_PANE).asButton)
 * }
 * ```
 *
 * @param player The player to create the GUI for
 * @param title The GUI title
 * @param customize Optional customization block
 * @return A configured GuiPage
 */
fun <T> List<T>.toPagedGui(
    player: Player,
    title: String = "Items",
    itemConverter: (T) -> Button = { Button.of(it as ItemStack) },
    customize: GuiPage.() -> Unit = {}
): GuiPage {
    val pagedGui = buildPagedGui {
        this.player = player
        line(6)
        title(title)
        setAllItems(this@toPagedGui.map(itemConverter))
        elementSlots = Slots.pattern(
            arrayOf(
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "OOOOOOOOO",
                "XXXXXXXXX"
            ), 'O'
        )
        simplePageSetting()
    }
    pagedGui.customize()
    return pagedGui
}

/**
 * Creates a confirmation dialog GUI with Yes/No buttons.
 *
 * Example:
 * ```kotlin
 * val confirmGui = confirmDialog(
 *     player = player,
 *     title = "Confirm Action",
 *     message = ItemStack(Material.PAPER).withName("§eAre you sure?"),
 *     onConfirm = { player.sendMessage("Confirmed!") },
 *     onCancel = { player.sendMessage("Cancelled!") }
 * )
 * confirmGui.openGui()
 * ```
 *
 * @param player The player to create the dialog for
 * @param title Dialog title
 * @param message ItemStack displayed as the message
 * @param confirmButton Custom confirm button (defaults to green glass)
 * @param cancelButton Custom cancel button (defaults to red glass)
 * @param onConfirm Action when confirmed
 * @param onCancel Action when cancelled
 * @return A configured confirmation dialog GUI
 */
fun confirmDialog(
    player: Player,
    title: String = "Confirm",
    message: ItemStack,
    confirmButton: ItemStack = ItemStack(Material.GREEN_STAINED_GLASS_PANE).apply {
        itemMeta = itemMeta?.apply { setDisplayName("§aConfirm") }
    },
    cancelButton: ItemStack = ItemStack(Material.RED_STAINED_GLASS_PANE).apply {
        itemMeta = itemMeta?.apply { setDisplayName("§cCancel") }
    },
    onConfirm: (Player) -> Unit,
    onCancel: (Player) -> Unit
): GuiCustom {
    return buildGui {
        this.player = player
        line(3)
        title(title)

        // Message in center
        draw {
            set(Slot.ofBukkit(1, 4), Button.of(message))
        }

        // Confirm button (left side)
        for (i in 0..2) {
            draw {
                set(Slot.ofBukkit(2, i), confirmButton.onPlayerClick {
                    onConfirm(it)
                    it.closeInventory()
                })
            }
        }

        // Cancel button (right side)
        for (i in 6..8) {
            draw {
                set(Slot.ofBukkit(2, i), cancelButton.onPlayerClick {
                    onCancel(it)
                    it.closeInventory()
                })
            }
        }

        // Fill rest with empty buttons
        fillEmpty(spacerButton())
    }
}

/**
 * Creates an animated button that cycles through multiple items.
 *
 * Note: Animation requires manual refresh calls or tick system integration.
 *
 * Example:
 * ```kotlin
 * val animatedSlot = 0
 * var frame = 0
 * val frames = listOf(
 *     ItemStack(Material.RED_WOOL),
 *     ItemStack(Material.YELLOW_WOOL),
 *     ItemStack(Material.GREEN_WOOL)
 * )
 *
 * // In your GUI tick or scheduler:
 * gui.draw().set(Slot.of(animatedSlot), Button.of(frames[frame++ % frames.size]))
 * ```
 */
class AnimatedButton(private val frames: List<ItemStack>, private val intervalTicks: Int = 20) {
    private var currentFrame = 0
    private var tickCounter = 0

    /**
     * Gets the current frame and advances to the next if interval has passed.
     */
    fun tick(): ItemStack {
        tickCounter++
        if (tickCounter >= intervalTicks) {
            currentFrame = (currentFrame + 1) % frames.size
            tickCounter = 0
        }
        return frames[currentFrame]
    }

    /**
     * Gets the current frame as a Button.
     */
    fun asButton(): Button = Button.of(frames[currentFrame])
}

/**
 * Extension to create an AnimatedButton from a list of ItemStacks.
 */
fun List<ItemStack>.asAnimatedButton(intervalTicks: Int = 20): AnimatedButton {
    return AnimatedButton(this, intervalTicks)
}
