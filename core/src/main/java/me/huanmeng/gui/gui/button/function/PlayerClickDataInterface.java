package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2025/1/8<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickDataInterface extends PlayerClickInterface {

    @Override
    default Result onClick(@NonNull ClickData clickData) {
        return onClick(clickData.player, clickData);
    }

    @NonNull
    Result onClick(Player player, @NonNull ClickData context);
}
