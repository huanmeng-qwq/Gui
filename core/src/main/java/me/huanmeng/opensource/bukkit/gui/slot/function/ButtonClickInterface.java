package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
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
