package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

@FunctionalInterface
public interface ButtonSimpleClickInterface extends ButtonClickInterface {
    @Override
    default Result onClick(Slot slot, Button button, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e) {
        return onPlayerClick(button, player);
    }

    Result onPlayerClick(Button button, Player player);
}
