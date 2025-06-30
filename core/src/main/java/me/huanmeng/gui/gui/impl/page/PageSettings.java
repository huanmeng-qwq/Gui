package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.gui.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.gui.impl.GuiPage;
import me.huanmeng.gui.util.item.ItemBuilder;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public interface PageSettings {
    /**
     * 构建一个普通的{@link PageSetting}
     *
     * @param gui gui
     * @return {@link PageSetting}
     */
    @NonNull
    static PageSetting normal(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea) {
        Button pre = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a上一页").build()
        );
        Button next = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a下一页").build()
        );
        return normal(gui, pageArea, pre, next);
    }

    @NonNull
    static PageSetting normal(@NonNull GuiPage gui) {
        return normal(gui, gui.defaultPageArea());
    }

    /**
     * 构建一个普通的{@link PageSetting}
     *
     * @param gui            gui
     * @param previousButton 上一页按钮
     * @param nextButton     下一页按钮
     * @return {@link PageSetting}
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
     * 构建一个普通的{@link PageSetting}
     *
     * @param gui            gui
     * @param previousButton 上一页按钮
     * @param nextButton     下一页按钮
     * @param previousSlot   上一页按钮的slot
     * @param nextSlot       下一页按钮的slot
     * @return {@link PageSetting}
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
