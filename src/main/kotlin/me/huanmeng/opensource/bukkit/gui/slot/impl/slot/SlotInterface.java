package me.huanmeng.opensource.bukkit.gui.slot.impl.slot;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class SlotInterface extends SlotImpl {
    @NonNull
    private final ButtonClickInterface clickInterface;
    @NonNull
    private final ButtonPlaceInterface buttonPlaceInterface;

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = (slot, player, button) -> true;
    }

    public SlotInterface(int index, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = (slot, button, player, click, action, slotType, slotKey, hotBarKey, e) -> button.onClick(slot, player, click, action, slotType, slotKey, hotBarKey, e);
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    @NotNull
    @Override
    public Result onClick(@NotNull Button button, @NotNull Player player, @NotNull ClickType click, @NotNull InventoryAction action, @NotNull InventoryType.SlotType slotType, int slot, int hotBarKey, @NotNull InventoryClickEvent e) {
        return clickInterface.onClick(this, button, player, click, action, slotType, slot, hotBarKey, e);
    }

    @Override
    public boolean tryPlace(@NotNull Button button, @NotNull Player player) {
        return buttonPlaceInterface.tryPlace(this, button, player);
    }
}
