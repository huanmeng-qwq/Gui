package me.huanmeng.opensource.bukkit.gui.slot.impl.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * 2024/12/7<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class SlotForward implements Slot {
    protected final Slot self;
    protected final Slot forwardSlot;
    @Nullable
    protected final ButtonPlaceInterface buttonPlaceInterface;

    public SlotForward(@NonNull Slot self, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface buttonPlaceInterface) {
        this.self = self;
        this.forwardSlot = forwardSlot;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotForward(@NonNull Slot self, @NonNull Slot forwardSlot) {
        this(self, forwardSlot, null);
    }

    @Override
    public int getIndex() {
        return self.getIndex();
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        return Result.forward(forwardSlot);
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

    @Override
    public boolean isPlayer() {
        return self.isPlayer();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SlotForward that = (SlotForward) o;
        return Objects.equals(self, that.self) && Objects.equals(forwardSlot, that.forwardSlot) && Objects.equals(buttonPlaceInterface, that.buttonPlaceInterface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self, forwardSlot, buttonPlaceInterface);
    }
}
