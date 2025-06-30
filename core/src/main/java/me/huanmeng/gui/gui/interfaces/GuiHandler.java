package me.huanmeng.gui.gui.interfaces;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.util.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/11/19<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiHandler {
    /**
     * 设置ItemStack到Inventory中
     */
    void onSetItem(@NonNull AbstractGui<?> gui, @NonNull Inventory inventory, @NonNull GuiButton button, @Nullable ItemStack itemStack);

    /**
     * 通过事件查询具体点击的是哪一个butoon
     *
     * @param e 事件
     * @return null时则使用默认底层判断获取
     */
    @Nullable
    default GuiButton queryClickButton(@NonNull InventoryClickEvent e, AbstractGui<?> gui) {
        return null;
    }

    class GuiHandlerDefaultImpl implements GuiHandler {

        @Override
        public void onSetItem(@NonNull AbstractGui<?> gui, @NonNull Inventory inventory, @NonNull GuiButton button, @Nullable ItemStack itemStack) {
            int index = button.getIndex();
            if (itemStack == null) {
                inventory.setItem(index, null);
                return;
            }
            ItemBuilder itemBuilder = new ItemBuilder(itemStack);
            inventory.setItem(index, itemBuilder.build());
        }
    }
}
