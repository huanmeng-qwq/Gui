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
public class GuiDraw<@NonNull G extends AbstractGui<G>> {
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
     * @param x1     minX
     * @param y1     minY
     * @param x2     maxX
     * @param y2     maxY
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> vertical(int x1, int y1, int x2, int y2, @Nullable Button button) {
        int[] xRange = MathUtil.range(x1, x2);
        int[] yRange = MathUtil.range(y1, y2);
        for (int x : xRange) {
            for (int y : yRange) {
                set(x, y, button);
            }
        }
        return this;
    }

    /**
     * 绘制按钮
     *
     * @param x      x
     * @param y      y
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> set(int x, int y, @Nullable Button button) {
        set(Slot.ofBukkit(x, y), button);
        return this;
    }

    /**
     * 绘制按钮
     *
     * @param slot   slot
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> set(int slot, @Nullable Button button) {
        set(Slot.of(slot), button);
        return this;
    }

    /**
     * 绘制按钮
     *
     * @param slots  slots
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> set(@NonNull Slots slots, @Nullable Button button) {
        for (Slot slot : slots.slots(gui)) {
            set(slot, button);
        }
        return this;
    }

    /**
     * 绘制按钮
     *
     * @param slots   slots
     * @param buttons 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> set(@NonNull Slots slots, @NonNull List<@Nullable ? extends Button> buttons) {
        Slot[] slotArray = slots.slots(gui);
        for (int i = 0; i < slotArray.length; i++) {
            if (i >= buttons.size()) {
                break;
            }
            set(slotArray[i], buttons.get(i));
        }
        return this;
    }

    /**
     * 绘制按钮
     *
     * @param slot   slot
     * @param button 按钮
     * @return this
     */
    @NonNull
    public GuiDraw<@NonNull G> set(@NonNull Slot slot, @Nullable Button button) {
        gui.addAttachedButton(new GuiButton(slot, button));
        return this;
    }
}
