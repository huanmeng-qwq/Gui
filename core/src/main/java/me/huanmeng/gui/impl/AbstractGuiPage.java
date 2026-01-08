package me.huanmeng.gui.impl;

import me.huanmeng.gui.GuiButton;
import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.impl.page.PageArea;
import me.huanmeng.gui.impl.page.PageButton;
import me.huanmeng.gui.impl.page.PageSetting;
import me.huanmeng.gui.slot.Slot;
import me.huanmeng.gui.slot.Slots;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Abstract base class for paginated GUI implementations.
 * <p>
 * AbstractGuiPage extends {@link AbstractGuiCustom} to provide pagination functionality.
 * It manages multiple {@link PageArea}s, each representing an independent paginated section
 * within the GUI. Each page area can have its own items, slots, pagination settings, and
 * navigation buttons.
 *
 * <p>
 * The class automatically handles:
 * <ul>
 *     <li>Distributing items across page areas based on available slots</li>
 *     <li>Rendering page navigation buttons (previous, next, first, last)</li>
 *     <li>Managing pagination state for multiple concurrent page areas</li>
 *     <li>Refreshing displayed items when page changes occur</li>
 * </ul>
 *
 * <p>
 * Implementations include {@link GuiPage} (single page area) and {@link CustomGuiPage}
 * (multiple page areas).
 *
 *
 * @param <G> the concrete GUI type for fluent method chaining
 * @author huanmeng_qwq
 * @since 2024/12/28
 * @see AbstractGuiCustom
 * @see GuiPage
 * @see CustomGuiPage
 * @see PageArea
 */
public abstract class AbstractGuiPage<G extends AbstractGuiPage<@NonNull G>> extends AbstractGuiCustom<G> {
    /**
     * List of page areas managed by this GUI.
     * Each page area represents an independent paginated section with its own items and settings.
     */
    protected List<PageArea> pageAreas = new ArrayList<>();

    /**
     * Creates a new paginated GUI for the specified player.
     *
     * @param player the player who will view this GUI
     */
    public AbstractGuiPage(@NonNull Player player) {
        super(player);
    }

    /**
     * Creates a new paginated GUI without a player.
     * <p>
     * The player must be set using {@link #setPlayer(Player)} before opening the GUI.
     *
     */
    public AbstractGuiPage() {
        super();
    }

    /**
     * Creates and registers a new page area with the specified configuration.
     * <p>
     * This is a convenience method that creates a PageArea with the given slots,
     * items, and elements per page, then adds it to this GUI.
     *
     *
     * @param slots the slots where items will be displayed
     * @param items the list of items to paginate
     * @param elementsPerPage the number of items to display per page
     * @return the newly created and registered PageArea
     */
    public PageArea pageArea(Slots slots, List<Button> items, int elementsPerPage) {
        return pageArea(new PageArea().slots(slots).items(items).elementsPerPage(elementsPerPage));
    }

    /**
     * Registers a page area with this GUI.
     * <p>
     * Once registered, the page area's items and navigation buttons will be
     * automatically rendered when the GUI is displayed.
     *
     *
     * @param pageArea the page area to register
     * @return the registered PageArea instance
     */
    public PageArea pageArea(PageArea pageArea) {
        this.pageAreas.add(pageArea);
        return pageArea;
    }

    /**
     * Retrieves all buttons to be displayed in the GUI, including page items and navigation buttons.
     * <p>
     * This method is called internally when the GUI is refreshed. It processes all registered
     * page areas to:
     * <ol>
     *     <li>Get the current page's items for each area</li>
     *     <li>Distribute items across the area's assigned slots</li>
     *     <li>Add page navigation buttons based on the area's PageSetting</li>
     *     <li>Apply conditional rendering based on button conditions (e.g., hide "next" on last page)</li>
     * </ol>
     *
     * <p>
     * Navigation buttons are added to {@link #editButtons} with highest priority, ensuring
     * they always appear above regular buttons. Regular buttons collection is cleared before
     * populating to ensure clean state.
     *
     *
     * @return a set of GuiButton instances representing all items and controls to display
     * @throws IllegalArgumentException if no player is set
     */
    @Override
    @NonNull
    protected Set<GuiButton> getFillItems() {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        buttons.clear();
        Set<GuiButton> buttons = new HashSet<>();
        for (PageArea pageArea : pageAreas) {
            refresh(pageArea.slots());
            List<Button> buttonList = pageArea.getCurrentItems();
            ArrayList<Slot> slots = new ArrayList<>(Arrays.asList(Objects.requireNonNull(pageArea.slots()).slots(self())));
            for (Button button : buttonList) {
                if (slots.isEmpty()) {
                    break;
                }
                Slot slot = slots.remove(0);
                buttons.add(new GuiButton(slot, button));
            }
            PageSetting pageSetting = pageArea.pageSetting();
            if (pageSetting != null) {
                for (Supplier<PageButton> supplier : pageSetting.pageButtons()) {
                    PageButton pageButton = supplier.get();
                    Slot slot = pageButton.slot().slot(line, pageArea, this);
                    editButtons.removeIf(button -> button.getSlot().equals(slot));
                    if (pageButton.condition().isAllow(pageArea.currentPage(), pageArea.getMaxPage(), pageArea, pageButton, player)) {
                        editButtons.add(new GuiButton(slot, pageButton));
                    }
                }
            }
        }
        return buttons;
    }

    /**
     * Copies all properties from this GUI to the target GUI instance.
     * <p>
     * In addition to copying standard GUI properties from the superclass,
     * this also copies all registered page areas. Note that the page areas
     * list is shallow-copied (new list, but same PageArea instances).
     *
     *
     * @param newGui the target GUI to copy properties to
     * @return the target GUI with copied properties
     */
    @Override
    protected G copy(Object newGui) {
        super.copy(newGui);
        G guiPage = (G) newGui;
        // GuiCustom
        guiPage.pageAreas = new ArrayList<>(pageAreas);

        return guiPage;
    }
}
