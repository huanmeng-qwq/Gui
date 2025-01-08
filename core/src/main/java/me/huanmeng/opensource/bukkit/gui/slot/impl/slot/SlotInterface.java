package me.huanmeng.opensource.bukkit.gui.slot.impl.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonClickInterface;
import me.huanmeng.opensource.bukkit.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class SlotInterface extends SlotImpl {
    @NonNull
    protected final ButtonClickInterface clickInterface;
    @NonNull
    protected final ButtonPlaceInterface buttonPlaceInterface;

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = ButtonPlaceInterface.ALWAYS_TRUE;
    }

    public SlotInterface(int index, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = (gui, slot, button, player, click, action, slotType, slotKey, hotBarKey, e) -> button.onClick(gui, slot, player, click, action, slotType, slotKey, hotBarKey, e);
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        return clickInterface.onClick(clickData);
    }

    @NonNull
    @Override
    public Result onClick(@NonNull AbstractGui<?> gui, @NonNull Button button, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBarKey, @NonNull InventoryClickEvent e) {
        return clickInterface.onClick(gui, this, button, player, click, action, slotType, slot, hotBarKey, e);
    }

    @Override
    public boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return buttonPlaceInterface.tryPlace(this, button, player);
    }
}
