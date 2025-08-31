package me.huanmeng.gui.gui.button.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface PlayerItemInterface {
    /**
     * 获取物品
     *
     * @param player 玩家
     * @return 物品
     */
    @Nullable
    ItemStack get(@NonNull Player player);

    static PlayerItemInterface of(ItemStack itemStack) {
        return new StaticItemProvider(itemStack);
    }

    class StaticItemProvider implements PlayerItemInterface {
        @Nullable
        private final ItemStack itemStack;

        public StaticItemProvider(@Nullable final ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public @Nullable ItemStack get(@NonNull Player player) {
            return this.itemStack;
        }
    }
}
