package me.huanmeng.opensource.bukkit.gui.draw;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.bukkit.util.MathUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 * Gui绘制器
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class GuiDraw<G extends AbstractGui<G>> {
    @NonNull
    private final G gui;

    public GuiDraw(@NonNull G gui) {
        this.gui = gui;
    }

    @NonNull
    public G gui() {
        return gui;
    }

    /**
     * 绘制按钮
     *
     * @param row1    minY
     * @param column1 minX
     * @param row2    maxY
     * @param column2 maxX
     * @param button  按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> vertical(int row1, int column1, int row2, int column2, @Nullable Button button) {
        int[] rowRange = MathUtil.range(row1, row2);
        int[] columnRange = MathUtil.range(column1, column2);
        for (int column : columnRange) {
            for (int row : rowRange) {
                set(row, column, button);
            }
        }
        return self();
    }

    /**
     * 绘制按钮
     *
     * @param column column
     * @param row    row
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> set(int row, int column, @Nullable Button button) {
        set(Slot.ofBukkit(row, column), button);
        return self();
    }

    /**
     * 绘制按钮
     *
     * @param slot   slot
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> set(int slot, @Nullable Button button) {
        set(Slot.of(slot), button);
        return self();
    }

    /**
     * 绘制按钮
     *
     * @param slots  slots
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slots slots, @Nullable Button button) {
        for (Slot slot : slots.slots(gui)) {
            set(slot, button);
        }
        return self();
    }

    /**
     * 绘制按钮
     *
     * @param slots   slots
     * @param buttons 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slots slots, @NonNull List<@Nullable ? extends Button> buttons) {
        Slot[] slotArray = slots.slots(gui);
        for (int i = 0; i < slotArray.length; i++) {
            if (i >= buttons.size()) {
                break;
            }
            set(slotArray[i], buttons.get(i));
        }
        return self();
    }

    /**
     * 绘制按钮
     *
     * @param slot   slot
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slot slot, @Nullable Button button) {
        gui.addAttachedButton(new GuiButton(slot, button));
        return self();
    }

    public GuiDraw<G> self() {
        return this;
    }
}
