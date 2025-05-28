package me.huanmeng.gui.gui.button;

import me.huanmeng.gui.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2024/12/27<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class SimpleItemButton implements Button {
    private final ItemStack itemStack;
    private final PlayerClickInterface clickable;

    public SimpleItemButton(ItemStack itemStack, PlayerClickInterface clickable) {
        this.itemStack = itemStack;
        this.clickable = clickable;
    }

    @Override
    public @Nullable ItemStack getShowItem(@NonNull Player player) {
        return this.itemStack;
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        if (clickable == null) {
            return Result.CANCEL;
        }
        return this.clickable.onClick(clickData);
    }

}
