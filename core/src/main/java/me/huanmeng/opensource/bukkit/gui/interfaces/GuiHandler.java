package me.huanmeng.opensource.bukkit.gui.interfaces;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

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
     * @deprecated {@link  #queryClickButton(InventoryClickEvent, AbstractGui)}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    @Nullable
    default GuiButton queryClickButton(@NonNull InventoryClickEvent e) {
        return null;
    }

    /**
     * 通过事件查询具体点击的是哪一个butoon
     *
     * @param e 事件
     * @return null时则使用默认底层判断获取
     */
    @Nullable
    default GuiButton queryClickButton(@NonNull InventoryClickEvent e, AbstractGui<?> gui) {
        return queryClickButton(e);
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

        @Override
        public @Nullable GuiButton queryClickButton(@NonNull InventoryClickEvent e, AbstractGui<?> gui) {
            return null;
        }

    }
}
