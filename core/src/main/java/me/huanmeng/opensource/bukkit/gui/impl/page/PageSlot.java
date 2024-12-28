package me.huanmeng.opensource.bukkit.gui.impl.page;

import me.huanmeng.opensource.bukkit.gui.impl.AbstractGuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/12/28<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public interface PageSlot {
    @NonNull
    Slot slot(int line, PageArea area, AbstractGuiPage<?> gui);

    static PageSlot of(Slot slot) {
        return (line, area, gui) -> slot;
    }
}
