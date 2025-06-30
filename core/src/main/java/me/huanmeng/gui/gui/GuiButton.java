package me.huanmeng.gui.gui;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    private boolean isPlayerInventory;

    public GuiButton(@NonNull Slot slot, @Nullable Button button) {
        this.slot = slot;
        this.button = button == null ? Button.empty() : button;
        this.isPlayerInventory = this.slot.isPlayer();
    }

    /**
     * 点击事件
     */
    @NonNull
    public Result onClick(@NonNull ClickData clickData) {
        return slot.onClick(clickData);
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

    public boolean isPlayerInventory() {
        return isPlayerInventory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuiButton guiButton = (GuiButton) o;
        return isPlayerInventory == guiButton.isPlayerInventory && Objects.equals(slot, guiButton.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, isPlayerInventory);
    }
}
