package me.huanmeng.opensource.bukkit.gui.enums;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.event.Cancellable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public enum Result {
    /**
     * 取消事件
     * {@link Cancellable#isCancelled()}
     */
    CANCEL(true),

    /**
     * 取消事件并刷新当前{@link Slot}的物品
     */
    CANCEL_UPDATE(CANCEL),
    /**
     * 取消事件并刷新所有的物品
     */
    CANCEL_UPDATE_ALL(CANCEL),
    /**
     * 取消时间并关闭GUI
     *
     * @see AbstractGui#close(boolean, boolean)
     */
    CANCEL_CLOSE(CANCEL),

    /**
     * 不取消事件
     */
    ALLOW(false),

    /**
     * 移除整个{@link org.bukkit.inventory.ItemStack}, 调用{@link AbstractGui#refresh}后物品将重置
     */
    CLEAR(CANCEL),

    /**
     * 物品数量递减, 调用{@link AbstractGui#refresh}后数量将重置
     */
    DECREMENT(CANCEL),

    /**
     * 物品数量递增, 调用{@link AbstractGui#refresh}后数量将重置
     */
    INCREMENTAL(CANCEL),

    ;

    private final boolean cancel;

    Result(boolean cancel) {
        this.cancel = cancel;
    }

    Result(Result result) {
        this.cancel = result.cancel;
    }

    public boolean isCancel() {
        return cancel;
    }
}
