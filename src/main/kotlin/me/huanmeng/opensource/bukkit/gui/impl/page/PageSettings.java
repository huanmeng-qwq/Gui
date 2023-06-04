package me.huanmeng.opensource.bukkit.gui.impl.page;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public interface PageSettings {
    @NonNull
    static PageSetting normal(@NonNull GuiPage gui) {
        Button pre = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a下一页").build()
        );
        Button next = Button.of(player ->
                new ItemBuilder(Material.ARROW, "§a上一页").build()
        );
        return normal(gui, pre, next);
    }

    @NonNull
    static PageSetting normal(@NonNull GuiPage gui, @NonNull Button previousButton, @NonNull Button nextButton) {
        return PageSetting.builder()
                .button(
                        PageButton.of(gui, previousButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), PageButtonTypes.PREVIOUS)
                )
                .button(
                        PageButton.of(gui, nextButton, PageCondition.simple(), PlayerClickPageButtonInterface.simple(), PageButtonTypes.NEXT)
                )
                .build();
    }
}
