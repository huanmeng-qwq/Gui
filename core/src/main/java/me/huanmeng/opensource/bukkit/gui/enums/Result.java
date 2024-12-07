
package me.huanmeng.opensource.bukkit.gui.enums;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.interfaces.CustomResultHandler;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 * 代表点击后返回的处理结果，该类定义了默认的，自定义结果处理的请使用{@link AbstractGui#customResultHandler(CustomResultHandler)}
 *
 * @author huanmeng_qwq
 */
public class Result {
    /**
     * 取消事件
     * {@link Cancellable#isCancelled()}
     */
    public static final Result CANCEL = new Result(true);

    /**
     * 取消事件并刷新当前{@link Slot}的物品
     */
    public static final Result CANCEL_UPDATE = new Result(CANCEL);

    /**
     * 取消事件并刷新所有的物品
     */
    public static final Result CANCEL_UPDATE_ALL = new Result(CANCEL);

    /**
     * 取消事件并关闭GUI
     *
     * @see AbstractGui#close(boolean, boolean)
     */
    public static final Result CANCEL_CLOSE = new Result(CANCEL);

    /**
     * 不取消事件
     */
    public static final Result ALLOW = new Result(false);

    /**
     * 移除整个{@link org.bukkit.inventory.ItemStack}, 调用{@link AbstractGui#refresh}后物品将重置
     */
    public static final Result CLEAR = new Result(CANCEL);

    /**
     * 物品数量递减, 调用{@link AbstractGui#refresh}后数量将重置
     */
    public static final Result DECREMENT = new Result(CANCEL);

    /**
     * 物品数量递增, 调用{@link AbstractGui#refresh}后数量将重置
     */
    public static final Result INCREMENTAL = new Result(CANCEL);

    private final boolean cancel;

    protected Result(boolean cancel) {
        this.cancel = cancel;
    }

    protected Result(Result result) {
        this.cancel = result.cancel;
    }

    public boolean isCancel() {
        return cancel;
    }

    public static Result forward(int forwardSlot, boolean cancel) {
        return new Forward(cancel, forwardSlot);
    }

    public static Result forward(int forwardSlot) {
        return new Forward(true, forwardSlot);
    }

    public static class Forward extends Result {
        private final int forward;

        protected Forward(boolean cancel, int forward) {
            super(cancel);
            this.forward = forward;
        }

        public Result forwardClick(@NonNull AbstractGui<?> gui, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBarKey, @NonNull InventoryClickEvent e) {
            GuiButton forwardButton = gui.getButton(forward);
            if (forwardButton == null) {
                return Result.ALLOW;
            }
            return forwardButton.onClick(gui, player, click, action, slotType, slot, hotBarKey, e);
        }
    }
}