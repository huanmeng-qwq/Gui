package me.huanmeng.gui.gui.button.function.page;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import me.huanmeng.gui.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.gui.impl.page.PageArea;
import me.huanmeng.gui.gui.impl.page.PageButtonType;
import org.jspecify.annotations.NonNull;

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
