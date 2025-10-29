package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Enum defining standard page navigation button types with their behaviors.
 * <p>
 * This enum provides four common navigation types used in paginated GUIs:
 * <ul>
 *   <li><b>PREVIOUS</b> - Navigate to the previous page (left-click, positioned at left)</li>
 *   <li><b>NEXT</b> - Navigate to the next page (left/right-click, positioned at right)</li>
 *   <li><b>FIRST</b> - Jump to the first page (shift-left-click, positioned at left)</li>
 *   <li><b>LAST</b> - Jump to the last page (shift-right-click, positioned at right)</li>
 * </ul>
 *
 * <p>
 * Each type implements {@link PageButtonType} and defines:
 * <ul>
 *   <li>Click types that trigger the action (main and optional sub-type)</li>
 *   <li>Page navigation logic (how to change the current page)</li>
 *   <li>Validation logic (when the button should be available)</li>
 *   <li>Recommended slot position</li>
 * </ul>
 *
 *
 * @author huanmeng_qwq
 * @see PageButtonType
 * @see PageButton
 */
public enum PageButtonTypes implements PageButtonType {
    /**
     * Previous page button - navigates backward by one page.
     * <p>
     * Click type: LEFT<br>
     * Position: Left side of the GUI (column 1)<br>
     * Enabled: When not on the first page
     * </p>
     */
    PREVIOUS(ClickType.LEFT, null) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.previousPage(1);
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.hasPreviousPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(line, 1);
        }
    },
    /**
     * Next page button - navigates forward by one page.
     * <p>
     * Click types: LEFT (main), RIGHT (sub)<br>
     * Position: Right side of the GUI (column 9)<br>
     * Enabled: When not on the last page
     * </p>
     */
    NEXT(ClickType.LEFT, ClickType.RIGHT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.nextPage(1);
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.hasNextPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(line, 9);
        }
    },
    /**
     * First page button - jumps to the first page.
     * <p>
     * Click types: LEFT (main), SHIFT_LEFT (sub)<br>
     * Position: Left side of the GUI (column 1)<br>
     * Enabled: When not already on the first page
     * </p>
     */
    FIRST(ClickType.LEFT, ClickType.SHIFT_LEFT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.setToFirstPage();
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.currentPage() > area.pagination().getMinPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(line, 1);
        }
    },
    /**
     * Last page button - jumps to the last page.
     * <p>
     * Click types: LEFT (main), SHIFT_RIGHT (sub)<br>
     * Position: Right side of the GUI (column 9)<br>
     * Enabled: When not already on the last page
     * </p>
     */
    LAST(ClickType.LEFT, ClickType.SHIFT_RIGHT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.setToLastPage();
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.currentPage() < area.getMaxPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(line, 9);
        }
    },
    ;

    /**
     * The primary click type that triggers this button.
     */
    @NonNull
    private final ClickType clickType;

    /**
     * The secondary click type for this button, if applicable.
     */
    @Nullable
    private final ClickType subType;

    /**
     * Constructs a PageButtonTypes enum constant.
     *
     * @param clickType the primary click type
     * @param subType   the secondary click type, or null
     */
    PageButtonTypes(@NonNull ClickType clickType, @Nullable ClickType subType) {
        this.clickType = clickType;
        this.subType = subType;
    }

    @Override
    @NonNull
    public ClickType mainType() {
        return clickType;
    }

    @Override
    @Nullable
    public ClickType subType() {
        return subType;
    }
}