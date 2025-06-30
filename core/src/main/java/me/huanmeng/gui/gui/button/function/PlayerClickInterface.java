package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickInterface {

    Result onClick(@NonNull ClickData clickData);

    static PlayerClickInterface dummy(Result result) {
        return new Dummy(result);
    }

    class Dummy implements PlayerClickInterface {
        final Result result;

        Dummy(Result result) {
            this.result = result;
        }

        @Override
        public Result onClick(@NonNull ClickData clickData) {
            return this.result;
        }
    }
}
