package me.huanmeng.gui;

import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Utility class for slot position calculations in Minecraft inventory GUIs.
 * <p>
 * This class provides static methods for converting between row/column coordinates
 * and Bukkit's slot indices, as well as calculating the recommended number of rows
 * for a given number of items.
 * <p>
 * Slot indices in Bukkit inventories are 0-based and increment left-to-right, top-to-bottom:
 * <pre>
 * Row 1: [0] [1] [2] [3] [4] [5] [6] [7] [8]
 * Row 2: [9] [10][11][12][13][14][15][16][17]
 * Row 3: [18][19][20][21][22][23][24][25][26]
 * </pre>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
public class SlotUtil {
    /**
     * Converts row and column coordinates to a Bukkit slot index.
     * <p>
     * Both row and column are 1-based (starting from 1, not 0).
     * For example, row 3 and column 5 corresponds to slot 22 in a chest inventory.
     *
     * @param row    the row number (1-based, where 1 is the top row)
     * @param column the column number (1-based, where 1 is the leftmost column)
     * @return the Bukkit slot index (0-based)
     */
    public static int getSlot(int row, int column) {
        return (row * 9) - (column == 9 ? 1 : 10 - column);
    }

    /**
     * Calculates the row number from a slot index.
     * <p>
     * The returned row number is 1-based (first row is 1).
     *
     * @param slot the Bukkit slot index (0-based)
     * @return the row number (1-based)
     */
    public static int getRow(int slot) {
        return (slot + 9) / 9;
    }

    /**
     * Calculates the column number from a slot index.
     * <p>
     * The returned column number is 1-based (first column is 1).
     *
     * @param slot the Bukkit slot index (0-based)
     * @return the column number (1-based, range 1-9)
     */
    public static int getColumn(int slot) {
        return (slot % 9) + 1;
    }

    /**
     * Calculates the recommended number of rows needed to display a collection of items.
     * <p>
     * This is a convenience wrapper for {@link #getLine(int)} that accepts a collection.
     *
     * @param items the collection of items to display
     * @return the recommended number of rows (minimum 1)
     */
    public static int getLine(@NonNull Collection<?> items) {
        return getLine(items.size());
    }

    /**
     * Calculates the recommended number of rows needed to display a given number of items.
     * <p>
     * This method is useful when creating GUIs that need to dynamically size based on
     * the number of items to display. Each row in a chest inventory has 9 slots.
     * <p>
     * Example: 10 items would require 2 rows, 18 items would require 2 rows,
     * 19 items would require 3 rows.
     *
     * @param size the number of items to display
     * @return the recommended number of rows (minimum 1 for size > 0)
     */
    public static int getLine(int size) {
        return (size % 9 == 0 ? size : size + 9) / 9;
    }
}
