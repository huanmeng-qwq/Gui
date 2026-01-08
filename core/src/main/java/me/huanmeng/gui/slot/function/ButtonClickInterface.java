package me.huanmeng.gui.slot.function;

import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface ButtonClickInterface {
    Result onClick(@NonNull ClickData clickData);
}
