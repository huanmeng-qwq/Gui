package me.huanmeng.opensource.bukkit.gui.button.function;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.ReplaceWith;
import org.jetbrains.annotations.ApiStatus;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 * @deprecated rename {@link PlayerItemInterface}
 */

@Deprecated(
        message = "use PlayerItemInterface",
        level = DeprecationLevel.HIDDEN,
        replaceWith = @ReplaceWith(
                expression = "PlayerItemInterface",
                imports = {
                        "me.huanmeng.opensource.bukkit.gui.button.function.PlayerItemInterface"
                }
        )
)
@ApiStatus.ScheduledForRemoval(inVersion = "2.5")
@java.lang.Deprecated
public interface UserItemInterface extends PlayerItemInterface {
}
