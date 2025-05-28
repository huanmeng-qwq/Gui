package me.huanmeng.gui.gui.impl.page;

import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface PageButtonType {
    /**
     * @return 主类型
     */
    @NonNull
    ClickType mainType();

    /**
     * 一个按钮存在多个{@link PageButtonType}时，使用此方法判断
     *
     * @return 次类型
     */
    @Nullable
    ClickType subType();

    void changePage(@NonNull PageArea area);

    /**
     * @return 要修改的页数是否存在与合法
     */
    boolean hasPage(@NonNull PageArea area);

    @NonNull
    Slot recommendSlot(int line);
}
