package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.impl.AbstractGuiPage;
import me.huanmeng.gui.gui.slot.Slot;
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

    static PageSlot ofGame(int row, int column) {
        return of(Slot.ofGame(row, column));
    }

    static PageSlot ofBukkit(int row, int column) {
        return of(Slot.ofBukkit(row, column));
    }

    static PageSlot of(int slotIndex) {
        return of(Slot.of(slotIndex));
    }
}
