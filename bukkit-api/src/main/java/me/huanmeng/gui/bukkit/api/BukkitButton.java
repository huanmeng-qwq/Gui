package me.huanmeng.gui.bukkit.api;

import java.util.Objects;
import java.util.function.Function;
import me.huanmeng.gui.api.ClickContext;
import me.huanmeng.gui.api.ClickResult;
import me.huanmeng.gui.api.Viewer;
import me.huanmeng.gui.api.layout.Button;
import org.bukkit.inventory.ItemStack;

public interface BukkitButton extends Button {
    ItemStack item(BukkitViewer viewer);

    @Override
    default BukkitItemView display(Viewer viewer) {
        if (!(viewer instanceof BukkitViewer)) {
            throw new IllegalArgumentException("viewer must be BukkitViewer");
        }
        return new BukkitItemView(item((BukkitViewer) viewer));
    }

    @Override
    default ClickResult click(ClickContext context) {
        if (!(context instanceof BukkitClickContext)) {
            throw new IllegalArgumentException("context must be BukkitClickContext");
        }
        return click((BukkitClickContext) context);
    }

    default ClickResult click(BukkitClickContext context) {
        return ClickResult.CANCEL;
    }

    static BukkitButton of(Function<? super BukkitViewer, ? extends ItemStack> itemProvider) {
        Objects.requireNonNull(itemProvider, "itemProvider");
        return viewer -> itemProvider.apply(viewer);
    }

    static BukkitButton of(Function<? super BukkitViewer, ? extends ItemStack> itemProvider,
                           Function<? super BukkitClickContext, ClickResult> clickHandler) {
        Objects.requireNonNull(itemProvider, "itemProvider");
        Objects.requireNonNull(clickHandler, "clickHandler");
        return new BukkitButton() {
            @Override
            public ItemStack item(BukkitViewer viewer) {
                return itemProvider.apply(viewer);
            }

            @Override
            public ClickResult click(BukkitClickContext context) {
                ClickResult result = clickHandler.apply(context);
                return result == null ? ClickResult.CANCEL : result;
            }
        };
    }
}
