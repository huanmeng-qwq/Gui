package me.huanmeng.opensource.bukkit.gui.enums;

import org.bukkit.event.Cancellable;

/**
 * 2022/6/16<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public enum Result {
    /**
     * 取消事件
     * {@link Cancellable#isCancelled()}
     */
    CANCEL(true),

    CANCEL_UPDATE(CANCEL),
    CANCEL_UPDATE_ALL(CANCEL),
    CANCEL_CLOSE(CANCEL),

    /**
     * 不取消事件
     */
    ALLOW(false),

    /**
     * 移除整个{@link org.bukkit.inventory.ItemStack}
     */
    CLEAR(CANCEL),

    /**
     * 物品数量递减
     */
    DECREMENT(CANCEL),

    /**
     * 物品数量递增
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
