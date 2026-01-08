package me.huanmeng.gui.impl.page;

import me.huanmeng.gui.slot.Slot;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface defining the behavior and characteristics of different page navigation button types.
 * <p>
 * PageButtonType defines how a navigation button behaves, including:
 * <ul>
 *   <li>Which click types trigger the navigation (main and sub types)</li>
 *   <li>How the page should change when clicked</li>
 *   <li>When the button should be enabled/disabled</li>
 *   <li>Where the button should be positioned</li>
 * </ul>
 *
 * <p>
 * Common implementations are provided in {@link PageButtonTypes} enum (PREVIOUS, NEXT, FIRST, LAST).
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/4
 * @see PageButtonTypes
 * @see PageButton
 */
public interface PageButtonType {
    /**
     * Gets the primary click type that triggers this button's navigation action.
     *
     * @return the main click type (e.g., LEFT_CLICK)
     */
    @NonNull
    ClickType mainType();

    /**
     * Gets the secondary click type for this button type.
     * <p>
     * When a PageButton has multiple PageButtonTypes, this method is used to determine
     * which type should handle the click based on the actual click type used by the player.
     * For example, SHIFT_LEFT might be a sub-type that triggers "go to first page".
     * </p>
     *
     * @return the secondary click type, or {@code null} if not applicable
     */
    @Nullable
    ClickType subType();

    /**
     * Performs the page navigation action on the given page area.
     * <p>
     * This method modifies the current page number in the PageArea according to
     * the button type's navigation behavior (e.g., increment for NEXT, decrement for PREVIOUS).
     * </p>
     *
     * @param area the page area to navigate
     */
    void changePage(@NonNull PageArea area);

    /**
     * Checks whether the target page for this navigation action exists and is valid.
     * <p>
     * This is used to determine whether the button should be displayed or enabled.
     * For example, a NEXT button should return {@code false} when already on the last page.
     * </p>
     *
     * @param area the page area to check
     * @return {@code true} if the navigation target page is valid, {@code false} otherwise
     */
    boolean hasPage(@NonNull PageArea area);

    /**
     * Gets the recommended slot position for this button type based on the GUI line count.
     * <p>
     * Different button types have conventional positions (e.g., PREVIOUS on the left,
     * NEXT on the right). This method returns the recommended position.
     * </p>
     *
     * @param line the number of lines/rows in the GUI
     * @return the recommended slot position
     */
    @NonNull
    Slot recommendSlot(int line);
}
