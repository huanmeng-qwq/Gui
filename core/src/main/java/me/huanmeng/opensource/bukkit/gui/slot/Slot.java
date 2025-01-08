package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.SlotUtil;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonSimpleClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slot.SlotForward;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slot.SlotImpl;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slot.SlotInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

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
    @NonNull
    default Result onClick(@NonNull ClickData clickData) {
        return clickData.button.onClick(clickData);
    }

    /**
     * 当点击时
     *
     * @return 是否取消事件
     * @see Result
     */
    @NonNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    default Result onClick(@NonNull AbstractGui<?> gui, @NonNull Button button, @NonNull Player player, @NonNull ClickType click,
                           @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot,
                           int hotBarKey, @NonNull InventoryClickEvent e) {
        return button.onClick(gui, this, player, click, action, slotType, slot, hotBarKey, e);
    }

    /**
     * 是否允许{@link Button}放置在当前Slot上
     */
    default boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return true;
    }

    /**
     * 以0开始
     */
    @Contract(value = "_ -> new", pure = true)
    static Slot of(int i) {
        return new SlotImpl(i);
    }

    /**
     * 以1开始
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofGame(int x, int y) {
        return new SlotImpl(SlotUtil.getSlot(y, x));
    }

    /**
     * 以0开始
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofBukkit(int x, int y) {
        return new SlotImpl(SlotUtil.getSlot(y + 1, x + 1));
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot of(int i, ButtonClickInterface buttonClickInterface) {
        return new SlotInterface(i, buttonClickInterface);
    }

    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int i, ButtonSimpleClickInterface buttonSimpleClickInterface) {
        return new SlotInterface(i, buttonSimpleClickInterface);
    }

    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int i, ButtonPlaceInterface placeInterface) {
        return new SlotInterface(i, placeInterface);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slot, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slot), Slot.of(forwardSlot), placeInterface);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slot, int forwardSlot) {
        return new SlotForward(Slot.of(slot), Slot.of(forwardSlot));
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slot, @NonNull Slot forwardSlot) {
        return new SlotForward(Slot.of(slot), forwardSlot);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot) {
        return new SlotForward(slot, Slot.of(forwardSlot));
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, @NonNull Slot forwardSlot) {
        return new SlotForward(slot, forwardSlot);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(slot, Slot.of(forwardSlot), placeInterface);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slot, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slot), forwardSlot, placeInterface);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(slot, forwardSlot, placeInterface);
    }

    @Nullable
    default ItemStack getShowingItem(@NonNull AbstractGui<?> gui, @NonNull Player player) {
        GuiButton button = gui.getButton(this);
        if (button == null) return null;
        return button.getButton().getShowItem(player);
    }

    /**
     * 玩家背包槽位
     *
     * @param index 背包槽位
     */
    static PlayerSlot player(int index) {
        return new PlayerSlot(Slot.of(index));
    }

    default PlayerSlot asPlayer() {
        return new PlayerSlot(this);
    }

    default boolean isPlayer() {
        return this instanceof PlayerSlot;
    }
}
