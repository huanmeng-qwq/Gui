package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.gui.slot.Slot;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Functional interface that determines the slot position for a page button based on the GUI configuration.
 * <p>
 * PageSlot provides a flexible way to calculate button positions dynamically, taking into account
 * the number of lines/rows in the GUI, the current page area, and the GUI instance itself.
 * This allows for adaptive button placement based on different GUI sizes.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * // Fixed slot position
 * PageSlot fixedSlot = PageSlot.of(Slot.of(45));
 *
 * // Dynamic slot based on GUI size
 * PageSlot dynamicSlot = (line, area, gui) -> Slot.ofGame(line, 5);
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2024/12/28
 */
public interface PageSlot {
    /**
     * Calculates the slot position for a page button.
     *
     * @param line the number of lines/rows in the GUI
     * @param area the page area being navigated
     * @param gui  the GUI instance
     * @return the calculated slot position
     */
    @NonNull
    Slot slot(int line, PageArea area, AbstractGuiPage<?> gui);

    /**
     * Creates a PageSlot that always returns the same fixed slot position.
     *
     * @param slot the fixed slot position
     * @return a PageSlot that always returns the specified slot
     */
    static PageSlot of(Slot slot) {
        return (line, area, gui) -> slot;
    }

    /**
     * Creates a PageSlot for a specific row and column position using game coordinates (1-indexed).
     *
     * @param row    the row number (1-indexed, where 1 is the top row)
     * @param column the column number (1-indexed, where 1 is the leftmost column)
     * @return a PageSlot for the specified position
     */
    static PageSlot ofGame(int row, int column) {
        return of(Slot.ofGame(row, column));
    }

    /**
     * Creates a PageSlot for a specific row and column position using Bukkit coordinates (0-indexed).
     *
     * @param row    the row number (0-indexed, where 0 is the top row)
     * @param column the column number (0-indexed, where 0 is the leftmost column)
     * @return a PageSlot for the specified position
     */
    static PageSlot ofBukkit(int row, int column) {
        return of(Slot.ofBukkit(row, column));
    }

    /**
     * Creates a PageSlot for a specific slot index.
     *
     * @param slotIndex the slot index (0-53 for a double chest)
     * @return a PageSlot for the specified index
     */
    static PageSlot of(int slotIndex) {
        return of(Slot.of(slotIndex));
    }
}
