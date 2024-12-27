package me.huanmeng.opensource.bukkit.gui.impl.page;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

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
    static PageSetting normal(@NonNull GuiPage gui) {
        Button pre = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a上一页").build()
        );
        Button next = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a下一页").build()
        );
        return normal(gui, pre, next);
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
    static PageSetting normal(@NonNull GuiPage gui, @NonNull Button previousButton, @NonNull Button nextButton) {
        return normal(gui, previousButton, nextButton, null, null);
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
    static PageSetting normal(@NonNull GuiPage gui, @NonNull Button previousButton, @NonNull Button nextButton,
                              @Nullable Function<@NonNull Integer, @NonNull Slot> previousSlot,
                              @Nullable Function<@NonNull Integer, @NonNull Slot> nextSlot) {
        return PageSetting.builder()
                .button(
                        PageButton.of(gui, previousButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), previousSlot, PageButtonTypes.PREVIOUS)
                )
                .button(
                        PageButton.of(gui, nextButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), nextSlot, PageButtonTypes.NEXT)
                )
                .build();
    }
}
