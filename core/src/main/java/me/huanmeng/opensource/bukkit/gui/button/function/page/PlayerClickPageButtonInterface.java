package me.huanmeng.opensource.bukkit.gui.button.function.page;

import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.impl.AbstractGuiPage;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageArea;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButtonType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickPageButtonInterface {

    /**
     * 点击事件
     *
     * @return {@link Result}
     */
    @NonNull
    Result onClick(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull PageButtonType buttonType, @NonNull ClickData clickData);

    PlayerClickPageButtonInterface SIMPLE = new PlayerClickPageButtonInterface() {
        @Override
        public @NonNull Result onClick(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull PageButtonType buttonType, @NonNull ClickData clickData) {
            buttonType.changePage(pageArea);
            return Result.CANCEL_UPDATE_ALL;
        }
    };

    static PlayerClickPageButtonInterface simple() {
        return SIMPLE;
    }
}
