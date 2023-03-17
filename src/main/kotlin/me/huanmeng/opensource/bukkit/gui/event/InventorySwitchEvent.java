package me.huanmeng.opensource.bukkit.gui.event;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class InventorySwitchEvent extends InventoryClickEvent {
    private boolean disable;

    public InventorySwitchEvent(InventoryView view, InventoryType.SlotType type, int slot, ClickType click, InventoryAction action) {
        super(view, type, slot, click, action);
    }

    public InventorySwitchEvent(InventoryView view, InventoryType.SlotType type, int slot, ClickType click, InventoryAction action, int key) {
        super(view, type, slot, click, action, key);
    }

    public boolean disable() {
        return disable;
    }

    public InventorySwitchEvent disable(boolean disable) {
        this.disable = disable;
        return this;
    }
}
