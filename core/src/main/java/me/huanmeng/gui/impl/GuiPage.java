package me.huanmeng.gui.impl;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.impl.page.PageArea;
import me.huanmeng.gui.impl.page.PageSetting;
import me.huanmeng.gui.slot.Slots;
import me.huanmeng.gui.page.Pagination;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * A paginated GUI implementation for displaying large collections of items across multiple pages.
 * <p>
 * GuiPage extends the basic GUI functionality to support pagination, allowing you to display
 * a large number of items that don't fit in a single inventory screen. Users can navigate
 * between pages using configurable navigation buttons.
 *
 * <p>
 * The GUI uses a single default {@link PageArea} to manage the pageable content. All items
 * are displayed within the slots defined by the elementSlots parameter, and page navigation
 * is handled automatically based on the configured {@link PageSetting}.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * List<Button> items = ...; // Your collection of buttons
 * Slots elementSlots = Slots.ofPattern(
 *     "OOOOOOOOO",  // O = item slots
 *     "OOOOOOOOO",
 *     "XXXXXXXXX"   // X = control/navigation area
 * );
 * GuiPage gui = new GuiPage(player, items, elementSlots);
 * gui.pageSetting(PageSettings.normal(gui));
 * gui.openGui();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see AbstractGuiPage
 * @see PageArea
 * @see PageSetting
 */
@SuppressWarnings("unused")
public class GuiPage extends AbstractGuiPage<GuiPage> {
    /**
     * The default page area that manages paginated content for this GUI.
     * This area contains all the items, slot configuration, and pagination logic.
     */
    protected PageArea defaultArea;

    /**
     * Creates a new paginated GUI with automatic elements per page calculation.
     * <p>
     * The number of elements per page is automatically set to the number of slots
     * available in the elementSlots parameter.
     * </p>
     *
     * @param player the player who will view this GUI
     * @param allItems the complete list of buttons to display across all pages
     * @param elementSlots the slots where page items will be displayed
     */
    public GuiPage(@NonNull Player player, @NonNull List<Button> allItems, @NonNull Slots elementSlots) {
        this(player, allItems, allItems.size(), elementSlots);
        this.defaultArea.elementsPerPage(elementSlots.slots(this).length);
    }

    /**
     * Creates a new paginated GUI with custom elements per page setting.
     * <p>
     * This constructor allows you to specify exactly how many items should be
     * displayed on each page, which may be less than the available slots.
     * </p>
     *
     * @param player the player who will view this GUI
     * @param allItems the complete list of buttons to display across all pages
     * @param elementsPerPage the number of items to display per page
     * @param elementSlots the slots where page items will be displayed
     */
    public GuiPage(@NonNull Player player, @NonNull List<Button> allItems, int elementsPerPage, @NonNull Slots elementSlots) {
        super(player);
        this.defaultArea = pageArea(elementSlots, allItems, elementsPerPage);
    }

    /**
     * Creates a new paginated GUI without a player or initial configuration.
     * <p>
     * The player, items, and slots must be configured before opening the GUI.
     * </p>
     */
    public GuiPage() {
        super();
        this.defaultArea = pageArea(new PageArea());
    }

    /**
     * Creates and refreshes the pagination for the default page area.
     * <p>
     * This method is called internally to initialize or update the pagination
     * state based on the current items and configuration.
     * </p>
     *
     * @return the refreshed pagination instance
     */
    @NonNull
    protected final Pagination<Button> createPagination() {
        this.defaultArea.refreshPagination();
        return this.defaultArea.pagination();
    }

    /**
     * Gets the pagination instance for the default page area.
     *
     * @return the current pagination instance
     */
    @NonNull
    public Pagination<Button> pagination() {
        return this.defaultArea.pagination();
    }

    /**
     * Sets the current page number.
     * <p>
     * Page numbers are 0-indexed. Setting the page will automatically refresh
     * the GUI to display the items for that page when opened or refreshed.
     * </p>
     *
     * @param page the page number to display (0-indexed)
     * @return this GuiPage instance for method chaining
     */
    @NonNull
    public GuiPage page(int page) {
        this.defaultArea.currentPage(page);
        return this;
    }

    /**
     * Configures the page navigation settings.
     * <p>
     * The PageSetting defines the navigation buttons (previous, next, first, last)
     * and their positions in the GUI. You can use predefined settings like
     * {@code PageSettings.normal(gui)} or create custom configurations.
     * </p>
     *
     * @param pageSetting the page navigation configuration
     * @return this GuiPage instance for method chaining
     * @see PageSetting
     */
    @NonNull
    public GuiPage pageSetting(@NonNull PageSetting pageSetting) {
        this.defaultArea.pageSetting(pageSetting);
        return this;
    }

    /**
     * Gets the current page number.
     *
     * @return the current page number (0-indexed)
     */
    public int page() {
        return this.defaultArea.currentPage();
    }

    /**
     * Gets the number of elements displayed per page.
     *
     * @return the number of elements per page
     */
    public int elementsPerPage() {
        return this.defaultArea.elementsPerPage();
    }

    /**
     * Advances to the next page by the specified number of pages.
     *
     * @param count the number of pages to advance
     */
    public void nextPage(int count) {
        this.defaultArea.nextPage(count);
    }

    /**
     * Goes back to the previous page by the specified number of pages.
     *
     * @param count the number of pages to go back
     */
    public void previousPage(int count) {
        this.defaultArea.previousPage(count);
    }

    /**
     * Navigates to the first page (page 0).
     */
    public void setToFirstPage() {
        this.defaultArea.setToFirstPage();
    }

    /**
     * Navigates to the last page.
     */
    public void setToLastPage() {
        this.defaultArea.setToLastPage();
    }

    /**
     * Checks if there is a previous page available.
     *
     * @return true if the current page is not the first page
     */
    public boolean hasPreviousPage() {
        return this.defaultArea.hasPreviousPage();
    }

    /**
     * Gets the pagination instance, guaranteed to be non-null.
     *
     * @return the pagination instance
     */
    @NonNull
    public Pagination<Button> getPaginationNotNull() {
        return this.defaultArea.pagination();
    }

    /**
     * Checks if there is a next page available.
     *
     * @return true if the current page is not the last page
     */
    public boolean hasNextPage() {
        return this.defaultArea.hasNextPage();
    }

    /**
     * Gets all items across all pages.
     *
     * @return the complete list of all items, or null if not set
     */
    @Nullable
    public List<Button> getAllItems() {
        return this.defaultArea.items();
    }

    /**
     * Sets all items to be displayed across all pages.
     * <p>
     * Setting new items will automatically recalculate the pagination.
     * The GUI must be refreshed to display the changes.
     * </p>
     *
     * @param allItems the complete list of items to display
     */
    public void setAllItems(@NonNull List<Button> allItems) {
        this.defaultArea.items(allItems);
    }

    /**
     * Sets the number of elements to display per page.
     * <p>
     * This will recalculate pagination automatically. The GUI must be
     * refreshed to display the changes.
     * </p>
     *
     * @param elementsPerPage the number of elements per page
     */
    public void setElementsPerPage(int elementsPerPage) {
        this.defaultArea.elementsPerPage(elementsPerPage);
    }

    /**
     * Gets the number of elements displayed per page.
     *
     * @return the number of elements per page
     */
    public int getElementsPerPage() {
        return this.defaultArea.elementsPerPage();
    }

    /**
     * Gets the slots where page elements are displayed.
     *
     * @return the element slots, or null if not set
     */
    @Nullable
    public Slots getElementSlots() {
        return this.defaultArea.slots();
    }

    /**
     * Sets the slots where page elements should be displayed.
     *
     * @param elementSlots the slots for displaying elements
     */
    public void setElementSlots(@Nullable Slots elementSlots) {
        this.defaultArea.slots(elementSlots);
    }

    /**
     * Gets the current page navigation settings.
     *
     * @return the page settings, or null if not set
     */
    @Nullable
    public PageSetting getPageSetting() {
        return this.defaultArea.pageSetting();
    }

    /**
     * Gets the default page area that manages all paginated content.
     *
     * @return the default page area
     */
    public PageArea defaultPageArea() {
        return this.defaultArea;
    }

    /**
     * Returns this instance for method chaining.
     *
     * @return this GuiPage instance
     */
    @NonNull
    @Override
    protected GuiPage self() {
        return this;
    }

    /**
     * Creates a deep copy of this GUI instance.
     *
     * @return a new GuiPage instance with copied properties
     */
    @Override
    public GuiPage copy() {
        return (GuiPage) super.copy();
    }

    /**
     * Creates a new empty GUI instance of the same type.
     *
     * @return a new empty GuiPage instance
     */
    @Override
    protected @NonNull GuiPage newGui() {
        return new GuiPage();
    }

    /**
     * Copies all properties from this GUI to the target GUI instance.
     *
     * @param newGui the target GUI to copy properties to
     * @return the target GUI with copied properties
     */
    @Override
    protected GuiPage copy(Object newGui) {
        super.copy(newGui);
        return (GuiPage) newGui;
    }
}
