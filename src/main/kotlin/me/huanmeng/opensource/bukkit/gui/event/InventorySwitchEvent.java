package me.huanmeng.opensource.bukkit.gui.event;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class InventorySwitchEvent extends InventoryClickEvent {
    private boolean disable;

    public InventorySwitchEvent(@NonNull InventoryView view, InventoryType.@NonNull SlotType type, int slot, @NonNull ClickType click, @NonNull InventoryAction action) {
        super(view, type, slot, click, action);
    }

    public InventorySwitchEvent(@NonNull InventoryView view, InventoryType.@NonNull SlotType type, int slot, @NonNull ClickType click, @NonNull InventoryAction action, int key) {
        super(view, type, slot, click, action, key);
    }

    public boolean disable() {
        return disable;
    }

    @NonNull
    public InventorySwitchEvent disable(boolean disable) {
        this.disable = disable;
        return this;
    }
}
