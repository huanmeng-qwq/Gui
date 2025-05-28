package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.button.Button;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PageCondition {
    /**
     * 是否允许绘制按钮
     *
     * @param currentPage 当前页数
     * @param maxPage     最大页数
     * @param page        gui
     * @param player      玩家
     * @return 允许绘制 {@link Button}
     * @see PageButton
     */
    boolean isAllow(int currentPage, int maxPage, @NonNull PageArea page, @NonNull PageButton button, @NonNull Player player);

    /**
     * 简单的判断，只要按钮存在一个符合{@link PageButtonType}的页数就允许
     */
    static PageCondition simple() {
        return (currentPage, maxPage, page, button, player) -> button.types().stream().anyMatch(type -> type.hasPage(page));
    }

    PageCondition DUMMY = (currentPage, maxPage, pageArea, button, player) -> true;

    static PageCondition dummy() {
        return DUMMY;
    }
}
