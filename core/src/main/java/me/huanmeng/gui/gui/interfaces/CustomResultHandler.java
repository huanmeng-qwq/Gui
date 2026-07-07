package me.huanmeng.gui.gui.interfaces;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.jspecify.annotations.NonNull;

/**
 * 2024/12/7<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public interface CustomResultHandler {
    <G extends AbstractGui<G>>
    void processResult(@NonNull Result result, @NonNull ClickData clickData);
}
