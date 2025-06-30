package me.huanmeng.gui.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiBottomClick {
    /**
     * 底部点击事件
     *
     * @param player    玩家
     * @param bottom    底部
     * @param clickType 点击类型
     * @param action    点击事件
     * @param slotType  位置类型
     * @param hotBar    热键
     * @param e         事件
     * @return 是否cancel事件
     */
    boolean onClickBottom(@NonNull Player player, @NonNull Inventory bottom, @NonNull ClickType clickType,
                          @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType,
                          int hotBar, @NonNull InventoryClickEvent e);
}
