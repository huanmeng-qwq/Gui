package me.huanmeng.opensource.bukkit.gui.interfaces;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2024/12/7<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public interface CustomResultHandler {
    <G extends AbstractGui<G>>
    void processResult(@NonNull Result result, AbstractGui<G> gui, @Nullable Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slot, int hotbarButton, @NonNull InventoryClickEvent e);
}
