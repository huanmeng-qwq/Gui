package me.huanmeng.opensource.bukkit.gui.button.function;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2025/1/8<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerClickDataInterface extends PlayerClickInterface {
    @Override
    default @NonNull Result onClick(@NonNull AbstractGui<?> gui, @NonNull Slot slot, @NonNull Player player, @NonNull ClickType click, @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slotKey, int hotBarKey, @NonNull InventoryClickEvent event) {
        return onClick(player, new ClickData(player, gui, slot, null, null, event, click, action, slotType, slotKey, hotBarKey));
    }

    @Override
    default Result onClick(@NonNull ClickData clickData) {
        return onClick(clickData.player, clickData);
    }

    @NonNull
    Result onClick(Player player, @NonNull ClickData context);
}
