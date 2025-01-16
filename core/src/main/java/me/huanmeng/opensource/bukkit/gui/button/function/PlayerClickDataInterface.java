package me.huanmeng.opensource.bukkit.gui.button.function;

import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
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
