package me.huanmeng.gui.gui.slot;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.gui.SlotUtil;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import me.huanmeng.gui.gui.slot.function.ButtonClickInterface;
import me.huanmeng.gui.gui.slot.function.ButtonPlaceInterface;
import me.huanmeng.gui.gui.slot.function.ButtonSimpleClickInterface;
import me.huanmeng.gui.gui.slot.impl.slot.SlotForward;
import me.huanmeng.gui.gui.slot.impl.slot.SlotImpl;
import me.huanmeng.gui.gui.slot.impl.slot.SlotInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
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
     * 是否允许{@link Button}放置在当前Slot上
     */
    default boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return true;
    }

    /**
     * 以0开始
     */
    @Contract(value = "_ -> new", pure = true)
    static Slot of(int slotIndex) {
        return new SlotImpl(slotIndex);
    }

    /**
     * 以1开始
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofGame(int row, int column) {
        return new SlotImpl(SlotUtil.getSlot(row, column));
    }

    /**
     * 以0开始
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Slot ofBukkit(int row, int column) {
        return new SlotImpl(SlotUtil.getSlot(row + 1, column + 1));
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot of(int slotIndex, ButtonClickInterface buttonClickInterface) {
        return new SlotInterface(slotIndex, buttonClickInterface);
    }

    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int slotIndex, ButtonSimpleClickInterface buttonSimpleClickInterface) {
        return new SlotInterface(slotIndex, buttonSimpleClickInterface);
    }

    @Contract(value = "_, !null -> new", pure = true)
    static Slot of(int slotIndex, ButtonPlaceInterface placeInterface) {
        return new SlotInterface(slotIndex, placeInterface);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slotIndex, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slotIndex), Slot.of(forwardSlot), placeInterface);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slotIndex, int forwardSlot) {
        return new SlotForward(Slot.of(slotIndex), Slot.of(forwardSlot));
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(int slotIndex, @NonNull Slot forwardSlot) {
        return new SlotForward(Slot.of(slotIndex), forwardSlot);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(int slotIndex, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(Slot.of(slotIndex), forwardSlot, placeInterface);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot) {
        return new SlotForward(slot, Slot.of(forwardSlot));
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, int forwardSlot, @Nullable ButtonPlaceInterface placeInterface) {
        return new SlotForward(slot, Slot.of(forwardSlot), placeInterface);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static Slot forward(@NonNull Slot slot, @NonNull Slot forwardSlot) {
        return new SlotForward(slot, forwardSlot);
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
     * @param slotIndex 背包槽位
     */
    static PlayerSlot player(int slotIndex) {
        return new PlayerSlot(Slot.of(slotIndex));
    }

    default PlayerSlot asPlayer() {
        return new PlayerSlot(this);
    }

    default boolean isPlayer() {
        return this instanceof PlayerSlot;
    }
}
