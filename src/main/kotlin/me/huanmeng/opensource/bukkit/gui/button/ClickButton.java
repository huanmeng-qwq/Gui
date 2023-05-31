package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickInterface;
import me.huanmeng.opensource.bukkit.gui.button.function.UserItemInterface;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * 2023/3/17<br>
 * Gui<br>
 * 点击型按钮
 *
 * @author huanmeng_qwq
 */
public class ClickButton implements Button {
    private final UserItemInterface userItemInterface;
    private final PlayerClickInterface playerClickInterface;

    public ClickButton(UserItemInterface userItemInterface, PlayerClickInterface playerClickInterface) {
        this.userItemInterface = userItemInterface;
        this.playerClickInterface = playerClickInterface;
    }

    @Override
    public ItemStack getShowItem(Player player) {
        return userItemInterface.get(player);
    }

    @Override
    public Result onClick(Slot slot, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e) {
        return playerClickInterface.onClick(slot, player, click, action, slotType, slotKey, hotBarKey);
    }
}
