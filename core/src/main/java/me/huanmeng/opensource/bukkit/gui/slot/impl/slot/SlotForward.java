package me.huanmeng.opensource.bukkit.gui.slot.impl.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2024/12/7<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class SlotForward extends SlotImpl {
    protected final int forwardSlot;
    @Nullable
    protected final ButtonPlaceInterface buttonPlaceInterface;

    public SlotForward(int index, int forwardSlot, @Nullable ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.forwardSlot = forwardSlot;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotForward(int index, int forwardSlot) {
        this(index, forwardSlot, null);
    }

    @Override
    public @NonNull Result onClick(@NonNull AbstractGui<?> gui, @NonNull Button button, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBarKey, @NonNull InventoryClickEvent e) {
        return Result.forward(forwardSlot);
    }

    @Override
    public boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        if (this.buttonPlaceInterface == null) return false;
        return this.buttonPlaceInterface.tryPlace(this, button, player);
    }
}
