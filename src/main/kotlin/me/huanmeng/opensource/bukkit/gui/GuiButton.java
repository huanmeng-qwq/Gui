package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public final class GuiButton {


    @NonNull
    private Slot slot;

    @NonNull
    private Button button;

    public GuiButton(@NonNull Slot slot, @Nullable Button button) {
        this.slot = slot;
        this.button = button == null ? Button.empty() : button;
    }

    /**
     * 点击事件
     */
    @NonNull
    public Result onClick(@NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action,
                          InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey,
                          @NonNull InventoryClickEvent e) {
        return slot.onClick(button, player, click, action, slotType, slotKey, hotBarKey, e);
    }

    public int getIndex() {
        return slot.getIndex();
    }

    public boolean canPlace(Player player) {
        return slot.tryPlace(getButton(), player);
    }

    @NonNull
    public Slot getSlot() {
        return slot;
    }

    public void setSlot(@NonNull Slot slot) {
        this.slot = slot;
    }

    @NonNull
    public Button getButton() {
        return button;
    }

    public void setButton(@NonNull Button button) {
        this.button = button;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GuiButton guiButton = (GuiButton) o;

        return Objects.equals(slot.getIndex(), guiButton.slot.getIndex());
    }

    @Override
    public int hashCode() {
        return slot.hashCode();
    }
}
