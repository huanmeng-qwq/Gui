package me.huanmeng.opensource.bukkit.gui.draw;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import org.apache.commons.lang.math.IntRange;

import java.util.List;

/**
 * 2022/6/17<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class GuiDraw<G extends AbstractGui<G>> {
    private final G gui;

    public GuiDraw(G gui) {
        this.gui = gui;
    }

    public G gui() {
        return gui;
    }

    public GuiDraw<G> vertical(int x1, int y1, int x2, int y2, Button button) {
        IntRange xRange = new IntRange(x1, x2);
        IntRange yRange = new IntRange(y1, y2);
        for (int x : xRange.toArray()) {
            for (int y : yRange.toArray()) {
                set(x, y, button);
            }
        }
        return this;
    }

    public GuiDraw<G> set(int x, int y, Button button) {
        set(Slot.ofBukkit(x, y), button);
        return this;
    }

    public GuiDraw<G> set(int slot, Button button) {
        set(Slot.of(slot), button);
        return this;
    }

    public GuiDraw<G> set(Slots slots, Button button) {
        for (Slot slot : slots.slots(gui)) {
            set(slot, button);
        }
        return this;
    }

    public GuiDraw<G> set(Slots slots, List<? extends Button> buttons) {
        Slot[] slotArray = slots.slots(gui);
        for (int i = 0; i < slotArray.length; i++) {
            if (i >= buttons.size()) {
                break;
            }
            set(slotArray[i], buttons.get(i));
        }
        return this;
    }

    public GuiDraw<G> set(Slot slot, Button button) {
        gui.addAttachedButton(new GuiButton(slot, button));
        return this;
    }
}
