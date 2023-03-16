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
public interface ButtonClickInterface {
    Result onClick(Slot slot, Button button, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e);
}
