package me.huanmeng.opensource.bukkit.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiEmptyItemClick {
    boolean onClickEmptyButton(Player player, int slot, ClickType clickType, InventoryAction action, InventoryType.SlotType slotType, int hotBar, InventoryClickEvent e);
}
