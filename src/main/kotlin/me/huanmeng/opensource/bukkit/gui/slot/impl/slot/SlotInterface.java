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

/**
 * 2022/6/17<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class SlotInterface extends SlotImpl {
    private final ButtonClickInterface clickInterface;
    private final ButtonPlaceInterface buttonPlaceInterface;

    public SlotInterface(int index, ButtonClickInterface clickInterface, ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotInterface(int index, ButtonClickInterface clickInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = (slot, player, button) -> true;
    }

    public SlotInterface(int index, ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = (slot, button, player, click, action, slotType, slotKey, hotBarKey, e) -> button.onClick(slot, player, click, action, slotType, slotKey, hotBarKey, e);
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    @Override
    public Result onClick(Button button, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slot, int hotBarKey, InventoryClickEvent e) {
        return clickInterface.onClick(this, button, player, click, action, slotType, slot, hotBarKey, e);
    }

    @Override
    public boolean tryPlace(Button button, Player player) {
        return buttonPlaceInterface.tryPlace(this, button, player);
    }
}
