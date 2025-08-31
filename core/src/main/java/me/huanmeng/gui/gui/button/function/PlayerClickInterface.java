package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
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

    static PlayerClickInterface of(Result result) {
        return new DefaultClickHandler(result);
    }

    static PlayerClickInterface of(Result result, Consumer<ClickData> clickDataConsumer) {
        return new DefaultClickHandler(result, clickDataConsumer);
    }

    static PlayerClickInterface handlePlayerClick(Result result, Consumer<Player> clickDataConsumer) {
        return new DefaultClickHandler(result, clickData -> clickDataConsumer.accept(clickData.player));
    }

    class DefaultClickHandler implements PlayerClickInterface {
        Consumer<ClickData> clickConsumer = null;
        final Result result;

        DefaultClickHandler(Result result) {
            this.result = result;
        }

        DefaultClickHandler(Result result, Consumer<ClickData> clickConsumer) {
            this.result = result;
            this.clickConsumer = clickConsumer;
        }

        @Override
        public Result onClick(@NonNull ClickData clickData) {
            if (clickConsumer != null) {
                clickConsumer.accept(clickData);
            }
            return this.result;
        }
    }
}
