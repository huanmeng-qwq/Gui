package me.huanmeng.gui.impl.page;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.slot.Slots;
import me.huanmeng.gui.page.Pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a pageable area within a GUI that displays a subset of items across multiple pages.
 * <p>
 * A PageArea manages the pagination logic for a set of buttons/items, including tracking the current page,
 * calculating available pages, and providing navigation methods. Each PageArea has defined slots where
 * items are displayed and can be configured with page navigation buttons through a PageSetting.
 *
 * <p>
 * This class is used by {@link me.huanmeng.gui.impl.GuiPage} and
 * {@link me.huanmeng.gui.impl.CustomGuiPage} to support paginated item display.
 *
 *
 * @author huanmeng_qwq
 * @since 2024/12/28
 */
public class PageArea {
    /**
     * The slots where paginated items will be displayed. Defaults to all slots.
     */
    protected Slots slots = Slots.full();

    /**
     * The complete list of items to be paginated across multiple pages.
     */
    protected List<Button> items = Collections.emptyList();

    /**
     * The maximum number of elements displayed per page.
     */
    protected int elementsPerPage;

    /**
     * The page setting that defines navigation buttons (previous, next, first, last).
     */
    protected PageSetting pageSetting;

    /**
     * The current page number being displayed (1-indexed).
     */
    protected int currentPage = 1;

    /**
     * The pagination instance that handles the logic of splitting items into pages.
     */
    protected Pagination<Button> pagination = new Pagination<>(items, elementsPerPage);

    /**
     * Navigates to the previous page by decrementing the current page by 1.
     */
    public void previousPage() {
        currentPage--;
    }

    /**
     * Navigates backwards by the specified number of pages.
     *
     * @param n the number of pages to go back
     */
    public void previousPage(int n) {
        currentPage -= n;
    }

    /**
     * Navigates to the next page by incrementing the current page by 1.
     */
    public void nextPage() {
        currentPage++;
    }

    /**
     * Navigates forwards by the specified number of pages.
     *
     * @param n the number of pages to advance
     */
    public void nextPage(int n) {
        currentPage += n;
    }

    /**
     * Jumps to the first page (minimum page number).
     */
    public void setToFirstPage() {
        currentPage = pagination.getMinPage();
    }

    /**
     * Jumps to the last page (maximum page number).
     */
    public void setToLastPage() {
        currentPage = pagination.getMaxPage();
    }

    /**
     * Checks if there is a previous page available from the current page.
     *
     * @return {@code true} if a previous page exists, {@code false} otherwise
     */
    public boolean hasPreviousPage() {
        return pagination.hasLast(currentPage);
    }

    /**
     * Checks if there is a next page available from the current page.
     *
     * @return {@code true} if a next page exists, {@code false} otherwise
     */
    public boolean hasNextPage() {
        return pagination.hasNext(currentPage);
    }

    /**
     * Gets the maximum page number based on the total items and elements per page.
     *
     * @return the maximum page number
     */
    public int getMaxPage() {
        return pagination.getMaxPage();
    }

    /**
     * Gets the list of items/buttons that should be displayed on the current page.
     *
     * @return a list of buttons for the current page
     */
    public List<Button> getCurrentItems() {
        return pagination.getElementsFor(currentPage);
    }

    /**
     * Gets the slots where paginated items are displayed.
     *
     * @return the slots configuration
     */
    public Slots slots() {
        return slots;
    }

    /**
     * Sets the slots where paginated items will be displayed.
     *
     * @param slots the slots configuration
     * @return this PageArea for method chaining
     */
    @CanIgnoreReturnValue
    public PageArea slots(Slots slots) {
        this.slots = slots;
        return this;
    }

    /**
     * Gets the complete list of items being paginated.
     *
     * @return the items list
     */
    public List<Button> items() {
        return items;
    }

    /**
     * Sets the items to be paginated and refreshes the pagination.
     *
     * @param items the list of items to paginate
     * @return this PageArea for method chaining
     */
    @CanIgnoreReturnValue
    public PageArea items(List<Button> items) {
        this.items = new ArrayList<>(items);
        refreshPagination();

        return this;
    }

    /**
     * Gets the number of elements displayed per page.
     *
     * @return the elements per page count
     */
    public int elementsPerPage() {
        return elementsPerPage;
    }

    /**
     * Sets the number of elements to display per page and refreshes the pagination.
     *
     * @param elementsPerPage the number of elements per page
     * @return this PageArea for method chaining
     */
    @CanIgnoreReturnValue
    public PageArea elementsPerPage(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
        refreshPagination();
        return this;
    }

    /**
     * Gets the current page number (1-indexed).
     *
     * @return the current page number
     */
    public int currentPage() {
        return currentPage;
    }

    /**
     * Sets the current page number.
     *
     * @param currentPage the page number to set (1-indexed)
     * @return this PageArea for method chaining
     */
    @CanIgnoreReturnValue
    public PageArea currentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    /**
     * Gets the underlying pagination instance.
     *
     * @return the pagination instance
     */
    public Pagination<Button> pagination() {
        return pagination;
    }

    /**
     * Gets the page setting that defines navigation buttons.
     *
     * @return the page setting, or {@code null} if not set
     */
    public PageSetting pageSetting() {
        return pageSetting;
    }

    /**
     * Sets the page setting that defines navigation buttons.
     *
     * @param pageSetting the page setting to use
     * @return this PageArea for method chaining
     */
    @CanIgnoreReturnValue
    public PageArea pageSetting(PageSetting pageSetting) {
        this.pageSetting = pageSetting;
        return this;
    }

    /**
     * Recreates the pagination instance with the current items and elements per page.
     * This should be called when items or elementsPerPage are modified.
     */
    public void refreshPagination() {
        pagination = new Pagination<>(items, elementsPerPage);
    }
}
