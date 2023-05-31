package me.huanmeng.opensource.bukkit.gui.interfaces;

import me.huanmeng.opensource.bukkit.gui.GuiButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiClick {
    /**
     * 点击事件, 预处理
     *
     * @param player    玩家
     * @param button    按钮
     * @param clickType 点击类型
     * @param action    点击事件
     * @param slotType  位置类型
     * @param slot      位置
     * @param hotBar    热键
     * @param e         事件
     * @return 是否允许点击
     */
    boolean allowClick(@NonNull Player player, @NonNull GuiButton button, @NonNull ClickType clickType,
                       @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot,
                       int hotBar, @NonNull InventoryClickEvent e);
}
