package me.huanmeng.gui.page;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic pagination utility for dividing collections into pages.
 * <p>
 * This class provides a simple and efficient way to paginate lists of items, commonly used
 * in GUI systems to display large collections across multiple pages. It handles page calculations,
 * element retrieval, and navigation logic.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * List<ItemStack> allItems = Arrays.asList(...); // 100 items
 * Pagination<ItemStack> pagination = new Pagination<>(allItems, 45); // 45 items per page
 *
 * // Display page 1
 * List<ItemStack> page1 = pagination.getElementsFor(1);
 *
 * // Check navigation
 * if (pagination.hasNext(1)) {
 *     // Show "Next Page" button
 * }
 *
 * // Total pages: Math.ceil(100 / 45) = 3 pages
 * int totalPages = pagination.getPages();
 * }</pre>
 *
 * <p>
 * <b>Page Numbering:</b> Pages are 1-indexed (first page is page 1, not 0).
 *
 *
 * @param <T> the type of elements being paginated
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
public class Pagination<@NonNull T> {
    @NonNull
    private List<@Nullable T> content;
    private final int elementsPerPage;

    /**
     * Constructs a new pagination instance.
     *
     * @param content          the full list of elements to paginate, never null
     * @param elementsPerPage  the maximum number of elements to display per page, must be positive
     * @throws IllegalArgumentException if elementsPerPage is less than 1
     */
    public Pagination(@NonNull List<T> content, int elementsPerPage) {
        this.content = content;
        this.elementsPerPage = elementsPerPage;
    }

    /**
     * Prints all elements on a specific page using the provided executor.
     * <p>
     * This method retrieves all elements for the given page and invokes the executor's
     * {@link IPaginationExecutor#print(Object)} method for each element in sequence.
     *
     *
     * @param page     the page number to print (1-indexed)
     * @param executor the executor that will process each element, never null
     */
    public void printPage(int page, @NonNull IPaginationExecutor<T> executor) {
        for (T element : getElementsFor(page)) {
            executor.print(element);
        }
    }

    /**
     * Retrieves all elements for a specific page.
     * <p>
     * This method calculates the start and end indices for the requested page and returns
     * a sublist containing only those elements. If the page number is invalid (less than 1
     * or greater than the total number of pages), an empty list is returned.
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * Pagination<String> p = new Pagination<>(Arrays.asList("A", "B", "C", "D", "E"), 2);
     * List<String> page1 = p.getElementsFor(1); // Returns ["A", "B"]
     * List<String> page2 = p.getElementsFor(2); // Returns ["C", "D"]
     * List<String> page3 = p.getElementsFor(3); // Returns ["E"]
     * List<String> page4 = p.getElementsFor(4); // Returns [] (out of bounds)
     * }</pre>
     *
     *
     * @param page the page number to retrieve (1-indexed)
     * @return a list containing the elements on the specified page, or an empty list if the page is invalid
     */
    @NonNull
    public List<T> getElementsFor(int page) {
        if (page <= 0 || page > getPages()) return new ArrayList<T>();

        int startIndex = (page - 1) * this.elementsPerPage;
        int endIndex = Math.min(page * this.elementsPerPage, this.content.size());

        return new ArrayList<>(this.content).subList(startIndex, Math.min(endIndex, this.content.size()));
    }

    /**
     * Calculates and returns the total number of pages.
     * <p>
     * The number of pages is calculated by dividing the total number of elements by the
     * elements per page, rounding up to ensure all elements are included.
     *
     * <p>
     * Formula: {@code Math.ceil(totalElements / elementsPerPage)}
     *
     *
     * @return the total number of pages (minimum 0 if content is empty)
     */
    public int getPages() {
        return (int) Math.ceil(content.size() / (double) elementsPerPage);
    }

    /**
     * Returns the maximum page number (same as {@link #getPages()}).
     * <p>
     * This is an alias for {@link #getPages()} provided for semantic clarity in navigation logic.
     *
     *
     * @return the highest valid page number
     */
    public int getMaxPage() {
        return getPages();
    }

    /**
     * Returns the minimum page number (always 1).
     * <p>
     * Pages are 1-indexed, so the minimum valid page number is always 1.
     *
     *
     * @return the lowest valid page number (always 1)
     */
    public int getMinPage() {
        return 1;
    }

    /**
     * Checks if there is a next page after the specified page.
     * <p>
     * This method is useful for determining whether to show a "Next Page" button in GUI navigation.
     *
     *
     * @param now the current page number (1-indexed)
     * @return {@code true} if there is a next page, {@code false} if the current page is the last page
     */
    public boolean hasNext(int now) {
        return now < getMaxPage();
    }

    /**
     * Checks if there is a previous page before the specified page.
     * <p>
     * This method is useful for determining whether to show a "Previous Page" button in GUI navigation.
     *
     *
     * @param now the current page number (1-indexed)
     * @return {@code true} if there is a previous page, {@code false} if the current page is the first page
     */
    public boolean hasLast(int now) {
        return now > getMinPage();
    }

    /**
     * Adds an element to the content list if it doesn't already exist.
     * <p>
     * This method uses {@link List#contains(Object)} to check for duplicates, so the element
     * will only be added if it's not already present in the list. Note that this may affect
     * the total number of pages returned by {@link #getPages()}.
     *
     *
     * @param element the element to add, may be null if the list supports null elements
     */
    public void addElement(@Nullable T element) {
        if (!this.content.contains(element)) this.content.add(element);
    }

    /**
     * Returns the complete list of elements being paginated.
     * <p>
     * Note: This returns a direct reference to the internal list, not a copy. Modifications
     * to the returned list will affect the pagination.
     *
     *
     * @return the full content list, never null
     */
    @NonNull
    public List<T> getContent() {
        return content;
    }

    /**
     * Replaces the entire content list with a new list.
     * <p>
     * This will immediately affect the pagination results, potentially changing the total
     * number of pages and the elements returned by {@link #getElementsFor(int)}.
     *
     *
     * @param content the new content list to paginate, never null
     */
    public void setContent(@NonNull List<T> content) {
        this.content = content;
    }

    /**
     * Returns the maximum number of elements displayed per page.
     * <p>
     * This value is set in the constructor and cannot be changed.
     *
     *
     * @return the number of elements per page
     */
    public int getElementsPerPage() {
        return elementsPerPage;
    }
}
