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
 * @param startRow Optional starting row (default: 0)
 * @param startCol Optional starting column (default: 0)
 * @param endRow Optional ending row (default: last row)
 * @param endCol Optional ending column (default: 8)
 */
fun <G : AbstractGuiCustom<G>> G.checkerboard(
    button1: Button,
    button2: Button,
    startRow: Int = 0,
    startCol: Int = 0,
    endRow: Int = -1,
    endCol: Int = 8
) {
    val totalSlots = size()
    val rows = totalSlots / 9
    val actualEndRow = if (endRow == -1) rows - 1 else minOf(endRow, rows - 1)
    val actualEndCol = minOf(endCol, 8)

    draw {
        for (row in startRow..actualEndRow) {
            for (col in startCol..actualEndCol) {
                val button = if ((row + col) % 2 == 0) button1 else button2
                set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Creates diagonal lines with customizable direction and thickness.
 *
 * Example:
 * ```kotlin
 * gui.diagonalLine(ItemStack(Material.GOLD_INGOT).asButton) // Primary diagonal
 * gui.diagonalLine(button = myButton, direction = DiagonalDirection.ANTI) // Anti-diagonal
 * gui.diagonalLine(button = myButton, thickness = 2) // Thick diagonal
 * ```
 *
 * @param button The button to use for the diagonal line
 * @param direction Direction of the diagonal (PRIMARY or ANTI)
 * @param thickness Thickness of the diagonal line (in slots)
 * @param startRow Optional starting row offset
 * @param startCol Optional starting column offset
 */
fun <G : AbstractGuiCustom<G>> G.diagonalLine(
    button: Button,
    direction: DiagonalDirection = DiagonalDirection.PRIMARY,
    thickness: Int = 1,
    startRow: Int = 0,
    startCol: Int = 0
) {
    val rows = size() / 9
    val maxIndex = minOf(rows, 9)

    draw {
        when (direction) {
            DiagonalDirection.PRIMARY -> {
                for (i in 0 until maxIndex) {
                    for (t in 0 until thickness) {
                        val row = startRow + i
                        val col = startCol + i + t
                        if (row < rows && col < 9) {
                            set(Slot.ofBukkit(row, col), button)
                        }
                    }
                }
            }
            DiagonalDirection.ANTI -> {
                for (i in 0 until maxIndex) {
                    for (t in 0 until thickness) {
                        val row = startRow + i
                        val col = 8 - startCol - i - t
                        if (row < rows && col >= 0) {
                            set(Slot.ofBukkit(row, col), button)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Direction for diagonal lines.
 */
enum class DiagonalDirection {
    /** Top-left to bottom-right */
    PRIMARY,
    /** Top-right to bottom-left */
    ANTI
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
 * gui.frame(sideButton = side, cornerButton = corner, thickness = 2) // Thick frame
 * ```
 *
 * @param sideButton Button for the sides
 * @param cornerButton Button for the corners
 * @param thickness Frame thickness in slots (default: 1)
 * @param margin Optional margin from edges
 */
fun <G : AbstractGuiCustom<G>> G.frame(
    sideButton: Button,
    cornerButton: Button,
    thickness: Int = 1,
    margin: Int = 0
) {
    val lines = size() / 9
    if (lines < 2 || thickness < 1) return

    draw {
        val innerMargin = thickness - 1 + margin

        // Corners
        for (t in 0 until thickness) {
            for (t2 in 0 until thickness) {
                set(Slot.ofBukkit(margin + t, margin + t2), cornerButton)
                set(Slot.ofBukkit(margin + t, 8 - margin - t2), cornerButton)
                if (lines > margin + thickness + t) {
                    set(Slot.ofBukkit(lines - 1 - margin - t, margin + t2), cornerButton)
                    set(Slot.ofBukkit(lines - 1 - margin - t, 8 - margin - t2), cornerButton)
                }
            }
        }

        // Top and bottom sides (excluding corners)
        for (col in margin + thickness..8 - margin - thickness) {
            for (t in 0 until thickness) {
                set(Slot.ofBukkit(margin + t, col), sideButton)
                if (lines > margin + thickness + t) {
                    set(Slot.ofBukkit(lines - 1 - margin - t, col), sideButton)
                }
            }
        }

        // Left and right sides (excluding corners)
        if (lines > margin + thickness * 2) {
            for (row in margin + thickness until lines - 1 - margin - thickness) {
                for (t in 0 until thickness) {
                    set(Slot.ofBukkit(row, margin + t), sideButton)
                    set(Slot.ofBukkit(row, 8 - margin - t), sideButton)
                }
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

/* Additional Advanced Layout Functions */

/**
 * Creates a cross pattern (+) with customizable size.
 *
 * Example:
 * ```kotlin
 * gui.cross(ItemStack(Material.RED_WOOL).asButton, centerRow = 2, centerCol = 4, size = 1)
 * ```
 *
 * @param button The button to use for the cross
 * @param centerRow Center row of the cross
 * @param centerCol Center column of the cross
 * @param size Size of the cross arms (in each direction)
 */
fun <G : AbstractGuiCustom<G>> G.cross(
    button: Button,
    centerRow: Int,
    centerCol: Int,
    size: Int = 1
) {
    val rows = size() / 9

    draw {
        // Horizontal line
        for (col in maxOf(0, centerCol - size)..minOf(8, centerCol + size)) {
            set(Slot.ofBukkit(centerRow, col), button)
        }

        // Vertical line
        for (row in maxOf(0, centerRow - size)..minOf(rows - 1, centerRow + size)) {
            set(Slot.ofBukkit(row, centerCol), button)
        }
    }
}

/**
 * Creates an X pattern (diagonal cross).
 *
 * Example:
 * ```kotlin
 * gui.xPattern(ItemStack(Material.DIAMOND_BLOCK).asButton, centerRow = 2, centerCol = 4, size = 2)
 * ```
 *
 * @param button The button to use for the X pattern
 * @param centerRow Center row of the X
 * @param centerCol Center column of the X
 * @param size Size of the X arms (in each direction)
 */
fun <G : AbstractGuiCustom<G>> G.xPattern(
    button: Button,
    centerRow: Int,
    centerCol: Int,
    size: Int = 1
) {
    val rows = size() / 9

    draw {
        // Primary diagonal
        for (i in -size..size) {
            val row = centerRow + i
            val col = centerCol + i
            if (row in 0 until rows && col in 0..8) {
                set(Slot.ofBukkit(row, col), button)
            }
        }

        // Anti-diagonal
        for (i in -size..size) {
            val row = centerRow + i
            val col = centerCol - i
            if (row in 0 until rows && col in 0..8) {
                set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Creates a filled rectangle with specified dimensions.
 *
 * Example:
 * ```kotlin
 * gui.rectangle(1, 1, 3, 7, ItemStack(Material.STONE).asButton)
 * ```
 *
 * @param startRow Starting row (0-based)
 * @param startCol Starting column (0-based)
 * @param width Width of the rectangle
 * @param height Height of the rectangle
 * @param button Button to fill the rectangle with
 */
fun <G : AbstractGuiCustom<G>> G.rectangle(
    startRow: Int,
    startCol: Int,
    width: Int,
    height: Int,
    button: Button
) {
    val rows = size() / 9
    val endRow = minOf(startRow + height - 1, rows - 1)
    val endCol = minOf(startCol + width - 1, 8)

    draw {
        for (row in startRow..endRow) {
            for (col in startCol..endCol) {
                set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Creates a hollow rectangle (outline only).
 *
 * Example:
 * ```kotlin
 * gui.hollowRectangle(1, 1, 7, 5, borderButton, fillButton)
 * ```
 *
 * @param startRow Starting row (0-based)
 * @param startCol Starting column (0-based)
 * @param width Width of the rectangle
 * @param height Height of the rectangle
 * @param borderButton Button for the border
 * @param fillButton Optional button for the interior (if provided, fills the interior)
 */
fun <G : AbstractGuiCustom<G>> G.hollowRectangle(
    startRow: Int,
    startCol: Int,
    width: Int,
    height: Int,
    borderButton: Button,
    fillButton: Button? = null
) {
    val rows = size() / 9
    val endRow = minOf(startRow + height - 1, rows - 1)
    val endCol = minOf(startCol + width - 1, 8)

    draw {
        // Top and bottom borders
        for (col in startCol..endCol) {
            set(Slot.ofBukkit(startRow, col), borderButton)
            set(Slot.ofBukkit(endRow, col), borderButton)
        }

        // Left and right borders
        for (row in startRow + 1 until endRow) {
            set(Slot.ofBukkit(row, startCol), borderButton)
            set(Slot.ofBukkit(row, endCol), borderButton)
        }

        // Fill interior if fillButton is provided
        if (fillButton != null && height > 2 && width > 2) {
            for (row in startRow + 1 until endRow) {
                for (col in startCol + 1 until endCol) {
                    set(Slot.ofBukkit(row, col), fillButton)
                }
            }
        }
    }
}

/**
 * Creates a zigzag pattern.
 *
 * Example:
 * ```kotlin
 * gui.zigzag(ItemStack(Material.YELLOW_WOOL).asButton, startRow = 0, amplitude = 3)
 * ```
 *
 * @param button The button to use for the zigzag
 * @param startRow Starting row for the pattern
 * @param amplitude Maximum horizontal displacement from center
 * @param wavelength Vertical distance between peaks
 */
fun <G : AbstractGuiCustom<G>> G.zigzag(
    button: Button,
    startRow: Int = 0,
    amplitude: Int = 3,
    wavelength: Int = 2
) {
    val rows = size() / 9

    draw {
        for (row in startRow until rows) {
            val phase = (row / wavelength) % 2
            val offset = if (phase == 0) amplitude else -amplitude
            val col = 4 + offset // Center column + offset

            if (col in 0..8) {
                set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Creates a gradient pattern using different buttons.
 *
 * Example:
 * ```kotlin
 * gui.gradient(startButton, endButton, direction = GradientDirection.HORIZONTAL)
 * ```
 *
 * @param startButton Button for the start of the gradient
 * @param endButton Button for the end of the gradient
 * @param direction Direction of the gradient
 * @param steps Number of steps in the gradient (default: auto-calculate)
 */
fun <G : AbstractGuiCustom<G>> G.gradient(
    startButton: Button,
    endButton: Button,
    direction: GradientDirection = GradientDirection.HORIZONTAL,
    steps: Int = -1
) {
    val rows = size() / 9
    val actualSteps = if (steps == -1) {
        when (direction) {
            GradientDirection.HORIZONTAL -> 9
            GradientDirection.VERTICAL -> rows
            GradientDirection.DIAGONAL -> minOf(9, rows)
        }
    } else steps

    draw {
        when (direction) {
            GradientDirection.HORIZONTAL -> {
                for (col in 0 until actualSteps) {
                    val button = if (col < actualSteps / 2) startButton else endButton
                    for (row in 0 until rows) {
                        set(Slot.ofBukkit(row, col), button)
                    }
                }
            }
            GradientDirection.VERTICAL -> {
                for (row in 0 until actualSteps) {
                    val button = if (row < actualSteps / 2) startButton else endButton
                    for (col in 0..8) {
                        set(Slot.ofBukkit(row, col), button)
                    }
                }
            }
            GradientDirection.DIAGONAL -> {
                for (i in 0 until actualSteps) {
                    val button = if (i < actualSteps / 2) startButton else endButton
                    set(Slot.ofBukkit(i, i), button)
                }
            }
        }
    }
}

/**
 * Direction for gradient patterns.
 */
enum class GradientDirection {
    /** Left to right */
    HORIZONTAL,
    /** Top to bottom */
    VERTICAL,
    /** Top-left to bottom-right */
    DIAGONAL
}

/**
 * Creates a heart shape pattern.
 *
 * Example:
 * ```kotlin
 * gui.heart(ItemStack(Material.RED_WOOL).asButton, centerRow = 2, centerCol = 4)
 * ```
 *
 * @param button The button to use for the heart
 * @param centerRow Center row of the heart
 * @param centerCol Center column of the heart
 * @param size Size of the heart (default: 1)
 */
fun <G : AbstractGuiCustom<G>> G.heart(
    button: Button,
    centerRow: Int,
    centerCol: Int,
    size: Int = 1
) {
    val rows = size() / 9

    // Heart pattern coordinates (simplified)
    val heartPattern = listOf(
        Pair(0, -1), Pair(0, 1),  // Top bumps
        Pair(-1, -2), Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(-1, 2),  // Upper row
        Pair(-2, -2), Pair(-2, -1), Pair(-2, 0), Pair(-2, 1), Pair(-2, 2),  // Middle row
        Pair(-3, -1), Pair(-3, 0), Pair(-3, 1),  // Lower middle
        Pair(-4, 0)  // Bottom point
    )

    draw {
        for ((dr, dc) in heartPattern) {
            for (s in 0 until size) {
                for (s2 in 0 until size) {
                    val row = centerRow + dr * size + s
                    val col = centerCol + dc * size + s2
                    if (row in 0 until rows && col in 0..8) {
                        set(Slot.ofBukkit(row, col), button)
                    }
                }
            }
        }
    }
}
