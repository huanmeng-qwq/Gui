package me.huanmeng.gui.impl.page;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.impl.GuiPage;
import me.huanmeng.gui.util.item.ItemBuilder;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory interface providing convenient methods to create common {@link PageSetting} configurations.
 * <p>
 * This interface provides static methods to quickly create standard page navigation setups with
 * previous/next buttons, using default or custom button appearances and positions.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * // Use default arrow buttons for previous/next
 * PageSetting setting = PageSettings.normal(gui);
 *
 * // Use custom buttons
 * Button customPrev = Button.of(...);
 * Button customNext = Button.of(...);
 * PageSetting setting = PageSettings.normal(gui, customPrev, customNext);
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/6/4
 * @see PageSetting
 * @see PageButton
 */
@SuppressWarnings("unused")
public interface PageSettings {
    /**
     * Creates a standard page setting with default previous and next buttons.
     * The buttons display as arrows with default names "Previous Page" and "Next Page".
     *
     * @param gui      the paginated GUI
     * @param pageArea the page area to navigate
     * @return a PageSetting with default previous/next buttons
     */
    @NonNull
    static PageSetting normal(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea) {
        return normal(gui, pageArea, "§aPrevious Page", "§aNext Page");
    }

    /**
     * Creates a standard page setting with custom names for previous and next buttons.
     *
     * @param gui          the paginated GUI
     * @param pageArea     the page area to navigate
     * @param previousName the display name for the previous page button
     * @param nextName     the display name for the next page button
     * @return a PageSetting with named previous/next buttons
     */
    @NonNull
    static PageSetting normal(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, String previousName, String nextName) {
        Button pre = Button.of(player ->
                new ItemBuilder(Material.ARROW, previousName).build()
        );
        Button next = Button.of(player ->
                new ItemBuilder(Material.ARROW, nextName).build()
        );
        return normal(gui, pageArea, pre, next);
    }

    @NonNull
    static PageSetting normal(@NonNull GuiPage gui) {
        return normal(gui, gui.defaultPageArea());
    }

    /**
     * Creates a standard page setting with custom previous and next button instances.
     *
     * @param gui            the paginated GUI
     * @param pageArea       the page area to navigate
     * @param previousButton the button to use for previous page navigation
     * @param nextButton     the button to use for next page navigation
     * @return a PageSetting with the specified buttons
     */
    @NonNull
    static PageSetting normal(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button previousButton, @NonNull Button nextButton) {
        return normal(gui, pageArea, previousButton, nextButton, null, null);
    }

    @NonNull
    static PageSetting normal(@NonNull GuiPage gui, @NonNull Button previousButton, @NonNull Button nextButton) {
        return normal(gui, gui.defaultPageArea(), previousButton, nextButton);
    }

    /**
     * Creates a standard page setting with custom buttons and slot positions.
     *
     * @param gui            the paginated GUI
     * @param pageArea       the page area to navigate
     * @param previousButton the button to use for previous page navigation
     * @param nextButton     the button to use for next page navigation
     * @param previousSlot   the slot position for the previous button, or null for default
     * @param nextSlot       the slot position for the next button, or null for default
     * @return a PageSetting with the specified buttons and positions
     */
    @NonNull
    static PageSetting normal(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button previousButton, @NonNull Button nextButton,
                              @Nullable PageSlot previousSlot,
                              @Nullable PageSlot nextSlot) {
        return PageSetting.builder()
                .button(
                        PageButton.of(gui, pageArea, previousButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), previousSlot, PageButtonTypes.PREVIOUS)
                )
                .button(
                        PageButton.of(gui, pageArea, nextButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), nextSlot, PageButtonTypes.NEXT)
                )
                .build();
    }

    @NonNull
    static PageSetting normal(@NonNull GuiPage gui, @NonNull Button previousButton, @NonNull Button nextButton,
                              @Nullable PageSlot previousSlot,
                              @Nullable PageSlot nextSlot) {
        return normal(gui, gui.defaultPageArea(), previousButton, nextButton, previousSlot, nextSlot);
    }
}
