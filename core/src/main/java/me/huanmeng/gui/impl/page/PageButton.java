package me.huanmeng.gui.impl.page;

import com.google.common.collect.ImmutableSet;
import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.gui.enums.Result;
import me.huanmeng.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.impl.GuiPage;
import me.huanmeng.gui.slot.Slot;
import me.huanmeng.gui.util.item.ItemUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A specialized button implementation for page navigation in paginated GUIs.
 * <p>
 * PageButton wraps an origin {@link Button} and adds pagination-specific behavior such as
 * page navigation (previous, next, first, last), conditional rendering based on page state,
 * and custom click handling for pagination operations.
 *
 * <p>
 * PageButtons are configured with one or more {@link PageButtonType}s that define their
 * navigation behavior (e.g., PREVIOUS, NEXT, FIRST, LAST). They can also have a
 * {@link PageCondition} that controls when they should be displayed (e.g., hide the "Next"
 * button when on the last page).
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * PageButton nextButton = PageButton.builder(gui)
 *     .button(Button.of(new ItemStack(Material.ARROW)))
 *     .condition(PageCondition.simple())
 *     .click(PlayerClickPageButtonInterface.simple())
 *     .types(PageButtonTypes.NEXT)
 *     .build();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/4
 * @see PageButtonType
 * @see PageCondition
 * @see PageArea
 */
@SuppressWarnings("unused")
public class PageButton implements Button {
    /**
     * The GUI this page button belongs to.
     */
    private AbstractGuiPage<?> gui;

    /**
     * The page area this button navigates.
     */
    private PageArea pageArea;

    /**
     * The underlying button that provides the visual representation and base behavior.
     */
    @Nullable
    private Button origin;

    /**
     * The condition that determines when this button should be displayed.
     */
    @Nullable
    private PageCondition condition;

    /**
     * The click handler for this page button.
     */
    @Nullable
    private PlayerClickPageButtonInterface playerClickPageButtonInterface;

    /**
     * The set of page button types (PREVIOUS, NEXT, FIRST, LAST) this button responds to.
     */
    private Set<@NonNull PageButtonType> types;

    /**
     * Defines the slot position for this button based on the GUI line count.
     * Defaults to using the first button type's recommended slot.
     */
    @NonNull
    private PageSlot slot = (line, area, gui) -> {
        if (types != null) {
            return types.stream().findFirst().map(type -> type.recommendSlot(line)).orElse(Slot.ofGame(line, 1));
        }
        return Slot.ofGame(line, 1);
    };

    private PageButton() {
    }

    /**
     * Creates a Builder instance initialized with this PageButton's current configuration.
     *
     * @return a Builder pre-populated with this button's settings
     */
    @NonNull
    public Builder toBuilder() {
        return new Builder(gui, pageArea)
                .button(origin)
                .condition(condition)
                .click(playerClickPageButtonInterface)
                .types(types.toArray(new PageButtonType[0]))
                .slot(slot)
                ;
    }

    /**
     * Creates a new Builder for constructing a PageButton.
     *
     * @param gui the paginated GUI this button belongs to
     * @return a new Builder instance
     */
    @NonNull
    public static Builder builder(@NonNull GuiPage gui) {
        return new Builder(gui);
    }

    /**
     * Creates a new Builder for constructing a PageButton with a specific page area.
     *
     * @param gui      the paginated GUI this button belongs to
     * @param area     the page area this button will navigate
     * @return a new Builder instance
     */
    @NonNull
    public static Builder builder(@NonNull AbstractGuiPage<?> gui, PageArea area) {
        return new Builder(gui, area);
    }

    /**
     * Creates a simple PageButton wrapping the given button.
     *
     * @param gui    the paginated GUI this button belongs to
     * @param button the button to wrap
     * @return a new PageButton instance
     */
    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button) {
        return PageButton.builder(gui).button(button).build();
    }

    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button) {
        return PageButton.builder(gui, pageArea).button(button).build();
    }

    /**
     * Creates a PageButton with a display condition.
     *
     * @param gui       the paginated GUI this button belongs to
     * @param pageArea  the page area this button will navigate
     * @param button    the button to wrap
     * @param condition the condition that controls when this button is displayed
     * @return a new PageButton instance
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition) {
        return PageButton.builder(gui, pageArea).button(button).condition(condition).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition) {
        return of(gui, gui.defaultPageArea(), button, condition);
    }

    /**
     * Creates a fully configured PageButton with condition, click handler, and types.
     *
     * @param gui       the paginated GUI this button belongs to
     * @param pageArea  the page area this button will navigate
     * @param button    the button to wrap
     * @param condition the condition that controls when this button is displayed
     * @param click     the click handler for pagination actions
     * @param types     the navigation types this button handles (PREVIOUS, NEXT, etc.)
     * @return a new PageButton instance
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition, @NonNull PlayerClickPageButtonInterface click, @NonNull PageButtonType... types) {
        return PageButton.builder(gui, pageArea)
                .button(button)
                .condition(condition)
                .click(click)
                .types(types).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition, @NonNull PlayerClickPageButtonInterface click, @NonNull PageButtonType... types) {
        return of(gui, gui.defaultPageArea(), button, condition, click, types);
    }

    /**
     * Creates a fully configured PageButton with all options including custom slot positioning.
     *
     * @param gui       the paginated GUI this button belongs to
     * @param pageArea  the page area this button will navigate
     * @param button    the button to wrap
     * @param condition the condition that controls when this button is displayed
     * @param click     the click handler for pagination actions
     * @param slot      the slot position provider for this button
     * @param types     the navigation types this button handles (PREVIOUS, NEXT, etc.)
     * @return a new PageButton instance
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition,
                                @NonNull PlayerClickPageButtonInterface click,
                                @Nullable PageSlot slot, @NonNull PageButtonType... types) {
        return PageButton.builder(gui, pageArea)
                .button(button)
                .condition(condition)
                .click(click)
                .types(types)
                .slot(slot).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition,
                                @NonNull PlayerClickPageButtonInterface click,
                                @Nullable PageSlot slot, @NonNull PageButtonType... types) {
        return of(gui, gui.defaultPageArea(), button, condition, click, slot, types);
    }

    /**
     * Gets the underlying button that provides visual representation.
     *
     * @return the origin button, or {@code null} if not set
     */
    @Nullable
    public Button origin() {
        return origin;
    }

    /**
     * Gets the condition that determines when this button should be displayed.
     *
     * @return the page condition, or a dummy condition if not set
     */
    @NonNull
    public PageCondition condition() {
        return condition != null ? condition : PageCondition.dummy();
    }

    /**
     * Gets the slot position provider for this button.
     *
     * @return the page slot provider
     */
    @NonNull
    public PageSlot slot() {
        return slot;
    }

    @Override
    public @Nullable ItemStack getShowItem(@NonNull Player player) {
        if (origin == null) return null;
        if (condition != null) {
            if (!condition.isAllow(pageArea.currentPage(), pageArea.getMaxPage(), pageArea, this, player)) {
                return null;
            }
        }
        return origin.getShowItem(player);
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        InventoryClickEvent e = clickData.event;
        if (ItemUtil.isAir(e.getCurrentItem())) {
            return Result.CANCEL;
        }
        if (origin == null) {
            return Result.CANCEL;
        }
        Result result = origin.onClick(clickData);
        if (playerClickPageButtonInterface == null || types.isEmpty()) {
            return result;
        }
        Iterator<PageButtonType> iterator = types.iterator();
        PageButtonType buttonType = iterator.next();
        while (iterator.hasNext()) {
            PageButtonType type = iterator.next();
            if (type.mainType() == buttonType.mainType() || type.subType() == clickData.click) {
                buttonType = type;
                break;
            }
        }
        if (!buttonType.hasPage(pageArea)) {
            return Result.CANCEL;
        }
        return playerClickPageButtonInterface.onClick(gui, pageArea, buttonType, clickData);
    }

    /**
     * Gets the set of page button types this button handles.
     *
     * @return the immutable set of button types (PREVIOUS, NEXT, FIRST, LAST)
     */
    @NonNull
    public Set<@NonNull PageButtonType> types() {
        return types;
    }

    /**
     * Builder class for constructing PageButton instances with fluent API.
     */
    public static class Builder {
        private final AbstractGuiPage<?> gui;
        private final PageArea pageArea;
        private PageSlot slot;
        private Button origin;
        private PageCondition condition;
        private PlayerClickPageButtonInterface playerClickPageButtonInterface;
        private final Set<@NonNull PageButtonType> types = new LinkedHashSet<>(PageButtonTypes.values().length);

        public Builder(@NonNull GuiPage gui) {
            this.gui = gui;
            this.pageArea = gui.defaultPageArea();
        }

        public Builder(@NonNull AbstractGuiPage<?> gui, PageArea pageArea) {
            this.gui = gui;
            this.pageArea = pageArea;
        }

        /**
         * Sets the underlying button that provides visual representation.
         *
         * @param origin the button to wrap
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder button(Button origin) {
            this.origin = origin;
            return this;
        }

        /**
         * Sets the condition that determines when this button should be displayed.
         *
         * @param condition the page condition
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder condition(PageCondition condition) {
            this.condition = condition;
            return this;
        }

        /**
         * Sets the click handler for pagination actions.
         *
         * @param playerClickPageButtonInterface the click handler
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder click(PlayerClickPageButtonInterface playerClickPageButtonInterface) {
            this.playerClickPageButtonInterface = playerClickPageButtonInterface;
            return this;
        }

        /**
         * Adds one or more page button types (PREVIOUS, NEXT, FIRST, LAST) this button handles.
         *
         * @param types the navigation types to add
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder types(@NonNull PageButtonType... types) {
            this.types.addAll(Arrays.asList(types));
            return this;
        }

        /**
         * Sets the slot position provider for this button.
         *
         * @param slot the page slot provider, or null to use default positioning
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder slot(@Nullable PageSlot slot) {
            this.slot = slot;
            return this;
        }

        /**
         * Builds and returns the configured PageButton instance.
         *
         * @return a new PageButton with the configured settings
         */
        @NonNull
        public PageButton build() {
            PageButton pageButton = new PageButton();
            pageButton.gui = this.gui;
            pageButton.pageArea = this.pageArea;
            pageButton.origin = this.origin;
            pageButton.condition = this.condition;
            pageButton.playerClickPageButtonInterface = this.playerClickPageButtonInterface;
            pageButton.types = ImmutableSet.copyOf(this.types);
            if (this.slot != null) {
                pageButton.slot = this.slot;
            }
            return pageButton;
        }
    }
}
