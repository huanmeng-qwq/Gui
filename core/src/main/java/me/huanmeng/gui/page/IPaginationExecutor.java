package me.huanmeng.gui.page;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Functional interface for processing individual elements during pagination printing.
 * <p>
 * This interface defines a single operation that is executed for each element on a page
 * when {@link Pagination#printPage(int, IPaginationExecutor)} is called. It's typically
 * used as a lambda or method reference to define how each paginated element should be
 * displayed or processed.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * Pagination<ItemStack> pagination = new Pagination<>(items, 45);
 *
 * // Lambda implementation
 * pagination.printPage(1, item -> {
 *     if (item != null) {
 *         gui.addButton(Button.of(item));
 *     }
 * });
 *
 * // Method reference
 * pagination.printPage(1, this::displayItem);
 * }</pre>
 *
 *
 * @param <T> the type of elements to process
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see Pagination#printPage(int, IPaginationExecutor)
 */
@FunctionalInterface
public interface IPaginationExecutor<@NonNull T> {
    /**
     * Processes a single element from a paginated list.
     * <p>
     * This method is called once for each element on the current page. The element
     * may be null if the pagination list allows null values.
     * </p>
     *
     * @param element the element to process, may be null depending on list configuration
     */
    void print(@Nullable T element);
}
