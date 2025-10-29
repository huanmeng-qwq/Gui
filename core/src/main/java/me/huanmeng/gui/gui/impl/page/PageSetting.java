package me.huanmeng.gui.gui.impl.page;

import com.google.common.collect.ImmutableList;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.impl.GuiPage;
import me.huanmeng.gui.func.FreezeSupplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Configuration class that defines the collection of navigation buttons for a paginated GUI.
 * <p>
 * PageSetting contains a list of {@link PageButton}s that provide navigation functionality
 * such as previous page, next page, first page, and last page buttons. These buttons are
 * applied to a {@link PageArea} to enable page navigation in the GUI.
 *
 * <p>
 * Use the {@link Builder} to construct a PageSetting with desired navigation buttons, or
 * use the convenient factory methods in {@link PageSettings} for common configurations.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/4
 * @see PageButton
 * @see PageSettings
 */
@SuppressWarnings("unused")
public class PageSetting {
    /**
     * The collection of page navigation button suppliers.
     */
    private List<@NonNull Supplier<@NonNull PageButton>> pageButtons;

    private PageSetting() {
    }

    /**
     * Creates a new Builder instance for constructing a PageSetting.
     *
     * @return a new Builder instance
     */
    @Contract(value = " -> new", pure = true)
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the collection of page navigation button suppliers.
     *
     * @return an immutable list of button suppliers
     */
    @NonNull
    public List<@NonNull Supplier<@NonNull PageButton>> pageButtons() {
        return pageButtons;
    }


    /**
     * Builder class for constructing PageSetting instances with navigation buttons.
     */
    public static class Builder {
        /**
         * The collection of page navigation button suppliers being built.
         */
        private List<@NonNull Supplier<@NonNull PageButton>> pageButtons;

        /**
         * Adds a page navigation button supplier to this setting.
         *
         * @param supplier the button supplier to add
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder button(@NonNull Supplier<@NonNull PageButton> supplier) {
            if (pageButtons == null) {
                pageButtons = new ArrayList<>();
            }
            this.pageButtons.add(supplier);
            return this;
        }

        /**
         * Adds a page navigation button to this setting.
         *
         * @param gui    the GUI this button belongs to
         * @param button the button to add
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder button(@NonNull GuiPage gui, @NonNull Button button) {
            return button(PageButton.of(gui, button));
        }

        /**
         * Adds a page navigation button with a display condition to this setting.
         *
         * @param gui       the GUI this button belongs to
         * @param button    the button to add
         * @param condition the condition that determines when this button should be displayed
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder button(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition) {
            return button(PageButton.of(gui, button, condition));
        }

        /**
         * Adds a PageButton instance to this setting.
         *
         * @param button the PageButton to add
         * @return this Builder for method chaining
         */
        @NonNull
        public Builder button(@NonNull PageButton button) {
            return this.button(FreezeSupplier.of(button));
        }

        /**
         * Builds and returns the configured PageSetting instance.
         *
         * @return a new PageSetting with the configured buttons
         */
        @NonNull
        public PageSetting build() {
            PageSetting pageSetting = new PageSetting();
            pageSetting.pageButtons = ImmutableList.copyOf(pageButtons);
            return pageSetting;
        }
    }
}
