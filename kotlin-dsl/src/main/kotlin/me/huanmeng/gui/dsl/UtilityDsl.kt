@file:Suppress("unused")

package me.huanmeng.gui.dsl

import me.huanmeng.gui.button.Button
import me.huanmeng.gui.impl.AbstractGuiCustom
import me.huanmeng.gui.slot.Slot
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/* Enhanced GUI Utility Functions */

/**
 * Creates a progress bar showing visual progress.
 *
 * Example:
 * ```kotlin
 * gui.progressBar(
 *     current = 5,
 *     max = 10,
 *     row = 0,
 *     startCol = 1,
 *     length = 7,
 *     fillButton = ItemStack(Material.GREEN_STAINED_GLASS_PANE).asButton,
 *     emptyButton = ItemStack(Material.GRAY_STAINED_GLASS_PANE).asButton
 * )
 * ```
 *
 * @param current Current progress value
 * @param max Maximum progress value
 * @param row Row to place the progress bar
 * @param startCol Starting column
 * @param length Length of the progress bar
 * @param fillButton Button for filled portion
 * @param emptyButton Button for empty portion
 */
fun <G : AbstractGuiCustom<G>> G.progressBar(
    current: Int,
    max: Int,
    row: Int,
    startCol: Int,
    length: Int,
    fillButton: Button,
    emptyButton: Button
) {
    require(max > 0) { "Max value must be positive" }
    require(current >= 0) { "Current value must be non-negative" }
    require(length > 0) { "Length must be positive" }
    require(startCol >= 0 && startCol + length <= 9) { "Invalid position or length" }

    val filledSlots = (current * length / max).coerceAtMost(length)

    draw {
        for (i in 0 until length) {
            val col = startCol + i
            val button = if (i < filledSlots) fillButton else emptyButton
            set(Slot.ofBukkit(row, col), button)
        }
    }
}

/**
 * Creates a tab menu with switchable content areas and click-to-switch functionality.
 *
 * Example:
 * ```kotlin
 * val tabs = listOf(
 *     Tab("Home", listOf(homeButtons...),
 *         ItemStack(Material.HOUSE).withName("§7Home"),
 *         ItemStack(Material.HOUSE).withName("§e§lHome")),
 *     Tab("Settings", listOf(settingsButtons...),
 *         ItemStack(Material.REDSTONE).withName("§7Settings"),
 *         ItemStack(Material.REDSTONE).withName("§e§lSettings"))
 * )
 * gui.tabMenu(tabs, selectedTabIndex = 0, contentRow = 2, contentCol = 1)
 * ```
 *
 * @param tabs List of tab configurations
 * @param selectedTabIndex Initially selected tab index
 * @param tabRow Row for tab buttons
 * @param tabStartCol Starting column for tab buttons
 * @param contentRow Starting row for content area
 * @param contentCol Starting column for content area
 * @param contentWidth Width of content area
 * @param contentHeight Height of content area
 * @param onTabSwitch Optional callback when tab is switched (receives new tab index)
 */
fun <G : AbstractGuiCustom<G>> G.tabMenu(
    tabs: List<Tab>,
    selectedTabIndex: Int = 0,
    tabRow: Int = 0,
    tabStartCol: Int = 0,
    contentRow: Int = 1,
    contentCol: Int = 0,
    contentWidth: Int = 9,
    contentHeight: Int = 3,
    onTabSwitch: ((Player, Int) -> Unit)? = null
) {
    require(tabs.isNotEmpty()) { "Tabs list cannot be empty" }
    require(selectedTabIndex in tabs.indices) { "Selected tab index out of range" }
    require(tabStartCol >= 0 && tabStartCol + tabs.size <= 9) { "Tab position invalid" }

    var currentTabIndex = selectedTabIndex.coerceIn(tabs.indices)

    // Function to refresh the tab menu
    fun refreshTabMenu(newTabIndex: Int) {
        currentTabIndex = newTabIndex

        // Redraw tab buttons
        draw {
            tabs.forEachIndexed { index, tab ->
                val col = tabStartCol + index
                val isSelected = index == currentTabIndex

                // Use appropriate button and add click handler
                val baseButton = if (isSelected) tab.selectedButton else tab.unselectedButton
                val clickableTabButton = baseButton.withClick { player ->
                    if (index != currentTabIndex) {
                        refreshTabMenu(index)
                        onTabSwitch?.invoke(player, index)
                    }
                }

                set(Slot.ofBukkit(tabRow, col), clickableTabButton)
            }
        }

        // Clear and redraw content area
        draw {
            for (i in 0 until contentWidth * contentHeight) {
                val row = contentRow + (i / contentWidth)
                val col = contentCol + (i % contentWidth)
                if (row < contentRow + contentHeight && col < contentCol + contentWidth) {
                    set(Slot.ofBukkit(row, col), spacerButton())
                }
            }
        }

        // Draw content for selected tab
        val selectedTab = tabs[currentTabIndex]
        draw {
            selectedTab.content.forEachIndexed { buttonIndex, button ->
                val row = contentRow + (buttonIndex / contentWidth)
                val col = contentCol + (buttonIndex % contentWidth)

                if (row < contentRow + contentHeight && col < contentCol + contentWidth) {
                    set(Slot.ofBukkit(row, col), button)
                }
            }
        }
    }

    // Initial render
    refreshTabMenu(currentTabIndex)
}

/**
 * Data class for tab configuration.
 */
data class Tab(
    val name: String,
    val content: List<Button>,
    val unselectedButton: Button,
    val selectedButton: Button
)

/**
 * Creates a simple tab menu with automatic styling.
 *
 * This is a simplified version that automatically creates selected/unselected button styles.
 *
 * Example:
 * ```kotlin
 * gui.simpleTabMenu(
 *     tabs = mapOf(
 *         "Home" to listOf(homeButtons...),
 *         "Shop" to listOf(shopButtons...),
 *         "Settings" to listOf(settingsButtons...)
 *     ),
 *     icons = mapOf(
 *         "Home" to Material.HOUSE,
 *         "Shop" to Material.EMERALD,
 *         "Settings" to Material.REDSTONE
 *     ),
 *     initialTab = "Home"
 * )
 * ```
 *
 * @param tabs Map of tab name to content buttons
 * @param icons Map of tab name to icon material
 * @param initialTab Initially selected tab name
 * @param tabRow Row for tab buttons
 * @param tabStartCol Starting column for tab buttons
 * @param contentRow Starting row for content area
 * @param contentCol Starting column for content area
 * @param contentWidth Width of content area
 * @param contentHeight Height of content area
 * @param selectedColor Color code for selected tab (default: §e§l = yellow bold)
 * @param unselectedColor Color code for unselected tab (default: §7 = gray)
 */
fun <G : AbstractGuiCustom<G>> G.simpleTabMenu(
    tabs: Map<String, List<Button>>,
    icons: Map<String, Material>,
    initialTab: String = tabs.keys.first(),
    tabRow: Int = 0,
    tabStartCol: Int = 0,
    contentRow: Int = 1,
    contentCol: Int = 0,
    contentWidth: Int = 9,
    contentHeight: Int = 3,
    selectedColor: String = "§e§l",
    unselectedColor: String = "§7"
) {
    require(tabs.isNotEmpty()) { "Tabs map cannot be empty" }
    require(initialTab in tabs.keys) { "Initial tab not found in tabs" }

    val tabList = tabs.keys.map { tabName ->
        val icon = icons[tabName] ?: Material.PAPER
        val content = tabs[tabName] ?: emptyList()

        Tab(
            name = tabName,
            content = content,
            unselectedButton = ItemStack(icon).withName("$unselectedColor$tabName"),
            selectedButton = ItemStack(icon).withName("$selectedColor$tabName")
        )
    }

    val initialIndex = tabs.keys.indexOf(initialTab)

    tabMenu(
        tabs = tabList,
        selectedTabIndex = initialIndex,
        tabRow = tabRow,
        tabStartCol = tabStartCol,
        contentRow = contentRow,
        contentCol = contentCol,
        contentWidth = contentWidth,
        contentHeight = contentHeight
    )
}

/**
 * Creates a hotbar with numbered slots (1-9).
 *
 * Example:
 * ```kotlin
 * gui.hotbar(
 *     row = 5,
 *     items = listOf(button1, button2, ..., button9),
 *     showNumbers = true
 * )
 * ```
 *
 * @param row Row to place the hotbar
 * @param items List of items (up to 9)
 * @param showNumbers Whether to show number indicators
 * @param emptySlot Button for empty slots
 */
fun <G : AbstractGuiCustom<G>> G.hotbar(
    row: Int,
    items: List<Button>,
    showNumbers: Boolean = true,
    emptySlot: Button = spacerButton()
) {
    require(row >= 0 && row < 6) { "Row must be between 0 and 5" }

    draw {
        for (col in 0..8) {
            val button = if (col < items.size) items[col] else emptySlot

            if (showNumbers && col < items.size) {
                // Add number lore if enabled
                set(Slot.ofBukkit(row, col), button)
            } else {
                set(Slot.ofBukkit(row, col), button)
            }
        }
    }
}

/**
 * Creates a crafting-like 3x3 grid.
 *
 * Example:
 * ```kotlin
 * gui.craftingGrid(
 *     startRow = 1,
 *     startCol = 1,
 *     gridButton = ItemStack(Material.AIR).asButton,
 *     resultButton = resultItem.asButton
 * )
 * ```
 *
 * @param startRow Starting row for the grid
 * @param startCol Starting column for the grid
 * @param gridButton Button for grid slots
 * @param resultButton Button for result slot (optional)
 * @param showBorder Whether to show a border around the grid
 * @param borderButton Button for border (if showBorder is true)
 */
fun <G : AbstractGuiCustom<G>> G.craftingGrid(
    startRow: Int,
    startCol: Int,
    gridButton: Button,
    resultButton: Button? = null,
    showBorder: Boolean = true,
    borderButton: Button = spacerButton()
) {
    require(startRow >= 0 && startRow <= 3) { "Invalid start row" }
    require(startCol >= 0 && startCol <= 6) { "Invalid start column" }

    if (showBorder) {
        // Draw border
        draw {
            for (row in startRow - 1..startRow + 3) {
                for (col in startCol - 1..startCol + 3) {
                    if (row < 0 || row >= 6 || col < 0 || col >= 9) continue

                    val isBorder = row == startRow - 1 || row == startRow + 3 ||
                                  col == startCol - 1 || col == startCol + 3
                    val button = if (isBorder) borderButton else gridButton

                    set(Slot.ofBukkit(row, col), button)
                }
            }
        }
    } else {
        // Draw only the 3x3 grid
        draw {
            for (row in startRow until startRow + 3) {
                for (col in startCol until startCol + 3) {
                    if (row < 6 && col < 9) {
                        set(Slot.ofBukkit(row, col), gridButton)
                    }
                }
            }
        }
    }

    // Add result slot if provided
    resultButton?.let {
        val resultRow = startRow + 1
        val resultCol = startCol + 5

        if (resultRow < 6 && resultCol < 9) {
            draw().set(Slot.ofBukkit(resultRow, resultCol), it)
        }
    }
}

/**
 * Creates a color palette for selection.
 *
 * Example:
 * ```kotlin
 * gui.colorPalette(
 *     row = 0,
 *     startCol = 0,
 *     colors = listOf(Material.RED_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL),
 *     onColorSelected = { player, material ->
 *         player.sendMessage("Selected: ${material.name}")
 *     }
 * )
 * ```
 *
 * @param row Row to place the palette
 * @param startCol Starting column
 * @param colors List of wool/dye materials representing colors
 * @param onColorSelected Callback when a color is selected
 * @param selectedBorder Border to show around selected color (optional)
 */
fun <G : AbstractGuiCustom<G>> G.colorPalette(
    row: Int,
    startCol: Int,
    colors: List<Material>,
    onColorSelected: (Player, Material) -> Unit,
    selectedBorder: Button? = null
) {
    require(row >= 0 && row < 6) { "Invalid row" }
    require(startCol >= 0 && startCol + colors.size <= 9) { "Invalid position or too many colors" }

    draw {
        colors.forEachIndexed { index, material ->
            val col = startCol + index
            val colorButton = ItemStack(material).onPlayerClick { player ->
                onColorSelected(player, material)
            }

            set(Slot.ofBukkit(row, col), colorButton)
        }
    }
}

/* Enhanced Item DSL Extensions */

/**
 * Creates an ItemStack with enchantments.
 *
 * Example:
 * ```kotlin
 * val enchantedSword = ItemStack(Material.DIAMOND_SWORD)
 *     .withEnchantments(Enchantment.DAMAGE_ALL to 5, Enchantment.DURABILITY to 3)
 * ```
 *
 * @param enchantments Pairs of Enchantment to level
 * @return This ItemStack with enchantments applied
 */
fun ItemStack.withEnchantments(vararg enchantments: Pair<Enchantment, Int>): ItemStack {
    return this.apply {
        enchantments.forEach { (enchant, level) ->
            addUnsafeEnchantment(enchant, level)
        }
    }
}

/**
 * Creates an ItemStack with item flags.
 *
 * Example:
 * ```kotlin
 * val cleanItem = ItemStack(Material.DIAMOND)
 *     .withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
 * ```
 *
 * @param flags Item flags to apply
 * @return This ItemStack with flags applied
 */
fun ItemStack.withFlags(vararg flags: ItemFlag): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply {
            flags.forEach { addItemFlags(it) }
        }
    }
}

/**
 * Creates an ItemStack with custom model data.
 *
 * Example:
 * ```kotlin
 * val customItem = ItemStack(Material.DIAMOND_AXE)
 *     .withCustomModelData(12345)
 * ```
 *
 * @param customModelData Custom model data value
 * @return This ItemStack with custom model data
 */
fun ItemStack.withCustomModelData(customModelData: Int): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply {
            setCustomModelData(customModelData)
        }
    }
}

/**
 * Creates a glowing item without enchantments.
 *
 * Example:
 * ```kotlin
 * val glowingItem = ItemStack(Material.DIAMOND).withGlow()
 * ```
 *
 * @return This ItemStack with a glowing effect
 */
fun ItemStack.withGlow(): ItemStack {
    return this.apply {
        addUnsafeEnchantment(Enchantment.DURABILITY, 1)
        itemMeta = itemMeta?.apply {
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }
}

/**
 * Creates an ItemStack that is unbreakable.
 *
 * Example:
 * ```kotlin
 * val unbreakableTool = ItemStack(Material.DIAMOND_PICKAXE).withUnbreakable()
 * ```
 *
 * @return This ItemStack as unbreakable
 */
fun ItemStack.withUnbreakable(): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply {
            isUnbreakable = true
            addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        }
    }
}

/**
 * Creates a button with durability (damage value).
 *
 * Example:
 * ```kotlin
 * val damagedTool = Material.DIAMOND_PICKAXE.asButton(durability = 500)
 * ```
 *
 * @param durability Durability value (0 = full durability, max = broken)
 * @return A Button with the specified durability
 */
fun Material.asButtonWithDurability(durability: Int): Button {
    val item = ItemStack(this).apply {
        this.durability = durability.toShort()
    }
    return Button.of(item)
}

/**
 * Creates a button with custom amount (stack size).
 *
 * Example:
 * ```kotlin
 * val stackOfItems = Material.DIAMOND.asButton(amount = 64)
 * ```
 *
 * @param amount Stack size (1-64)
 * @return A Button with the specified amount
 */
fun Material.asButtonWithAmount(amount: Int): Button {
    val item = ItemStack(this, amount.coerceIn(1, 64))
    return Button.of(item)
}

/**
 * Creates a button with amount and display name.
 *
 * Example:
 * ```kotlin
 * val namedStack = Material.GOLD_INGOT.asButtonWithAmount(32, "§6Gold Coins")
 * ```
 *
 * @param amount Stack size (1-64)
 * @param displayName Display name for the item
 * @return A Button with the specified amount and name
 */
fun Material.asButtonWithAmount(amount: Int, displayName: String): Button {
    val item = ItemStack(this, amount.coerceIn(1, 64)).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(displayName)
        }
    }
    return Button.of(item)
}