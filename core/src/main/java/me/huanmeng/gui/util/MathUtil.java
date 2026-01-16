package me.huanmeng.gui.util;

import me.huanmeng.gui.SlotUtil;

import java.util.stream.IntStream;

/**
 * Utility class providing mathematical helper methods for GUI operations.
 * <p>
 * This class contains static utility methods for generating integer ranges
 * and calculating slot arrays for rectangular areas in inventory GUIs.
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
public final class MathUtil {
    /**
     * Generates an array of integers within the specified range.
     * <p>
     * The range is inclusive of the minimum value and exclusive of the maximum value,
     * following Java's standard range conventions.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (exclusive)
     * @return an array containing all integers from min (inclusive) to max (exclusive)
     */
    public static int[] range(int min, int max) {
        return IntStream.range(min, max).toArray();
    }

    /**
     * Calculates an array of slot indices for a rectangular area defined by row and column ranges.
     * <p>
     * This method generates all slot indices within the rectangle defined by the corners
     * (a1, b1) and (a2, b2), where a represents rows and b represents columns.
     * Both ranges are inclusive.
     * <p>
     * The slot indices are calculated using {@link SlotUtil#getSlot(int, int)}.
     *
     * @param a1 the starting row (inclusive, 1-based)
     * @param a2 the ending row (inclusive, 1-based)
     * @param b1 the starting column (inclusive, 1-based)
     * @param b2 the ending column (inclusive, 1-based)
     * @return an array of slot indices covering the rectangular area
     * @throws IllegalArgumentException if a1 > a2 or b1 > b2
     */
    public static int[] cut(int a1, int a2, int b1, int b2) {
        if (a1 > a2 || b1 > b2) {
            throw new IllegalArgumentException("Invalid interval: start should be less than or equal to end.");
        }

        int rowCount = a2 - a1 + 1;
        int colCount = b2 - b1 + 1;
        int totalSlots = rowCount * colCount;

        int[] slots = new int[totalSlots];
        int index = 0;

        for (int row = a1; row <= a2; row++) {
            for (int col = b1; col <= b2; col++) {
                slots[index++] = SlotUtil.getSlot(row, col);
            }
        }

        return slots;
    }
}
