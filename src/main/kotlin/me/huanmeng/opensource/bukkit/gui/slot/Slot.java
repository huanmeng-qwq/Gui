package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.SlotUtil;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonSimpleClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slot.SlotImpl;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slot.SlotInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("rawtypes")
public interface Slot {
    /**
     * 位置
     */
    int getIndex();

    /**
     * 当点击时
     *
     * @return 是否取消事件
     * @see Result
     */
    default Result onClick(Button button, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slot, int hotBarKey, InventoryClickEvent e) {
        return button.onClick(this, player, click, action, slotType, slot, hotBarKey, e);
    }

    /**
     * 是否允许{@link Button}放置在当前Slot上
     */
    default boolean tryPlace(Button button, Player player) {
        return true;
    }

    /**
     * 以0开始
     */
    static Slot of(int i) {
        return new SlotImpl(i);
    }

    /**
     * 以1开始
     */
    static Slot ofGame(int x, int y) {
        return new SlotImpl(SlotUtil.getSlot(y, x));
    }

    /**
     * 以0开始
     */
    static Slot ofBukkit(int x, int y) {
        return new SlotImpl(SlotUtil.getSlot(y + 1, x + 1));
    }

    static Slot of(int i, ButtonClickInterface buttonClickInterface) {
        return new SlotInterface(i, buttonClickInterface);
    }

    static Slot of(int i, ButtonSimpleClickInterface buttonSimpleClickInterface) {
        return new SlotInterface(i, buttonSimpleClickInterface);
    }

    static Slot of(int i, ButtonPlaceInterface placeInterface) {
        return new SlotInterface(i, placeInterface);
    }

    default ItemStack getShowingItem(AbstractGui gui, Player player) {
        return gui.getButton(getIndex()).getButton().getShowItem(player);
    }
}
